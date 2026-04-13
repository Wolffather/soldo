package ru.savvy.soldo.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.bot.dto.BotBookingRequest;
import ru.savvy.soldo.bot.dto.BotBookingResponse;
import ru.savvy.soldo.bot.dto.BotCategoryResponse;
import ru.savvy.soldo.bot.dto.BotEventResponse;
import ru.savvy.soldo.bot.service.dialog.BotDialogState;
import ru.savvy.soldo.bot.service.dialog.BotDialogStateService;
import ru.savvy.soldo.bot.service.dialog.BotDialogStep;
import ru.savvy.soldo.notification.service.TelegramSenderService;
import ru.savvy.soldo.shared.exception.IllegalOperationException;
import ru.savvy.soldo.shared.exception.NotFoundException;
import ru.savvy.soldo.tenant.TenantConfigRepository;
import ru.savvy.soldo.tenant.TenantContext;
import ru.savvy.soldo.tenant.TenantRepository;
import ru.savvy.soldo.tenant.model.Tenant;
import ru.savvy.soldo.tenant.model.TenantConfig;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Обработчик webhook-обновлений Telegram. Реализует полный диалог бронирования:
 * <ol>
 *     <li>Пользователь жмёт /start — бот показывает меню.</li>
 *     <li>Выбор категории → список событий.</li>
 *     <li>Выбор события → запрос имени.</li>
 *     <li>Ввод имени → запрос телефона.</li>
 *     <li>Ввод телефона → подтверждение.</li>
 *     <li>Подтверждение → создаётся гостевое бронирование.</li>
 * </ol>
 * Дополнительно: /mybookings — список активных записей, отмена через inline-кнопку.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramWebhookServiceImpl implements TelegramWebhookService {

    // Callback data prefixes
    private static final String CB_BOOK_START = "book_start";
    private static final String CB_MY_BOOKINGS = "my_bookings";
    private static final String CB_CATEGORY = "cat:";      // cat:{id}
    private static final String CB_EVENT = "event:";       // event:{id}
    private static final String CB_CONFIRM = "confirm";
    private static final String CB_CANCEL_FLOW = "cancel_flow";
    private static final String CB_CANCEL_BOOKING = "cancel_booking:"; // cancel_booking:{id}

    private final TenantRepository tenantRepository;
    private final TenantConfigRepository tenantConfigRepository;
    private final BotBookingService botBookingService;
    private final BotDialogStateService dialogStateService;
    private final TelegramSenderService telegramSender;

    @Override
    @Transactional
    public void handleUpdate(String tenantSlug, String secretFromHeader, Map<String, Object> update) {
        Tenant tenant = tenantRepository.findBySlug(tenantSlug)
                .orElseThrow(() -> new NotFoundException("Тенант не найден: " + tenantSlug));

        TenantConfig config = tenantConfigRepository.findById(tenant.getId()).orElse(null);
        if (config == null || config.getTelegramBotToken() == null) {
            log.warn("Webhook для тенанта {}, но бот не настроен", tenantSlug);
            return;
        }

        String expected = config.getTelegramWebhookSecret();
        if (expected != null && !expected.isBlank() && !expected.equals(secretFromHeader)) {
            log.warn("Webhook: некорректный secret token для тенанта {}", tenantSlug);
            return;
        }

        TenantContext.setCurrentTenantId(tenant.getId());
        try {
            processUpdate(tenant, update);
        } finally {
            TenantContext.clear();
        }
    }

    @SuppressWarnings("unchecked")
    private void processUpdate(Tenant tenant, Map<String, Object> update) {
        Object callback = update.get("callback_query");
        if (callback instanceof Map<?, ?> cbMap) {
            handleCallbackQuery(tenant, (Map<String, Object>) cbMap);
            return;
        }

        Object messageObj = update.get("message");
        if (!(messageObj instanceof Map<?, ?> message)) return;

        Object chatObj = message.get("chat");
        if (!(chatObj instanceof Map<?, ?> chat)) return;
        Object chatIdObj = chat.get("id");
        if (!(chatIdObj instanceof Number chatIdNum)) return;
        Long chatId = chatIdNum.longValue();

        Object textObj = message.get("text");
        if (!(textObj instanceof String text)) {
            sendDefault(tenant, chatId);
            return;
        }

        handleTextMessage(tenant, chatId, text.trim());
    }

    // ─── Text messages ────────────────────────────────────────────────────────

    private void handleTextMessage(Tenant tenant, Long chatId, String text) {
        String lower = text.toLowerCase();
        BotDialogState state = dialogStateService.getOrCreate(tenant.getId(), chatId);

        // Команды имеют приоритет над состоянием диалога
        if (lower.startsWith("/start")) {
            state.reset();
            // Поддержка deep-link: /start booking_<id>
            String param = text.length() > "/start".length()
                    ? text.substring("/start".length()).trim()
                    : "";
            if (param.startsWith("booking_")) {
                handleBookingDeepLink(tenant, chatId, param.substring("booking_".length()));
                return;
            }
            sendWelcome(tenant, chatId);
            return;
        }
        if (lower.startsWith("/help")) {
            sendHelp(tenant, chatId);
            return;
        }
        if (lower.startsWith("/book")) {
            startBookingFlow(tenant, chatId, state);
            return;
        }
        if (lower.startsWith("/mybookings") || lower.startsWith("/my")) {
            sendMyBookings(tenant, chatId);
            return;
        }
        if (lower.startsWith("/cancel")) {
            state.reset();
            telegramSender.sendMessage(tenant.getId(), chatId, "Действие отменено.");
            return;
        }

        // Диалог: ожидание ввода текста
        switch (state.getStep()) {
            case ENTERING_NAME -> {
                if (text.length() < 2) {
                    telegramSender.sendMessage(tenant.getId(), chatId,
                            "Имя слишком короткое. Введите ваше имя:");
                    return;
                }
                state.setGuestName(text);
                state.setStep(BotDialogStep.ENTERING_PHONE);
                telegramSender.sendMessage(tenant.getId(), chatId,
                        "Укажите ваш номер телефона (например, +79991234567):");
            }
            case ENTERING_PHONE -> {
                String phone = text.replaceAll("[^+\\d]", "");
                if (phone.length() < 10) {
                    telegramSender.sendMessage(tenant.getId(), chatId,
                            "Номер выглядит некорректно. Попробуйте ещё раз:");
                    return;
                }
                state.setGuestPhone(phone);
                state.setStep(BotDialogStep.CONFIRMING);
                sendConfirmation(tenant, chatId, state);
            }
            default -> sendDefault(tenant, chatId);
        }
    }

    // ─── Callback queries ─────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private void handleCallbackQuery(Tenant tenant, Map<String, Object> callback) {
        String callbackId = asString(callback.get("id"));
        Object data = callback.get("data");
        if (!(data instanceof String dataStr)) {
            telegramSender.answerCallbackQuery(tenant.getId(), callbackId, null);
            return;
        }

        Object message = callback.get("message");
        if (!(message instanceof Map<?, ?> msgMap)) {
            telegramSender.answerCallbackQuery(tenant.getId(), callbackId, null);
            return;
        }
        Object chat = ((Map<String, Object>) msgMap).get("chat");
        if (!(chat instanceof Map<?, ?> chatMap)) {
            telegramSender.answerCallbackQuery(tenant.getId(), callbackId, null);
            return;
        }
        Object chatIdObj = ((Map<String, Object>) chatMap).get("id");
        if (!(chatIdObj instanceof Number chatIdNum)) {
            telegramSender.answerCallbackQuery(tenant.getId(), callbackId, null);
            return;
        }
        Long chatId = chatIdNum.longValue();

        BotDialogState state = dialogStateService.getOrCreate(tenant.getId(), chatId);
        try {
            if (dataStr.equals(CB_BOOK_START)) {
                startBookingFlow(tenant, chatId, state);
            } else if (dataStr.equals(CB_MY_BOOKINGS)) {
                sendMyBookings(tenant, chatId);
            } else if (dataStr.startsWith(CB_CATEGORY)) {
                Long categoryId = parseLong(dataStr.substring(CB_CATEGORY.length()));
                if (categoryId != null) onCategorySelected(tenant, chatId, state, categoryId);
            } else if (dataStr.startsWith(CB_EVENT)) {
                Long eventId = parseLong(dataStr.substring(CB_EVENT.length()));
                if (eventId != null) onEventSelected(tenant, chatId, state, eventId);
            } else if (dataStr.equals(CB_CONFIRM)) {
                onConfirm(tenant, chatId, state);
            } else if (dataStr.equals(CB_CANCEL_FLOW)) {
                state.reset();
                telegramSender.sendMessage(tenant.getId(), chatId, "Запись отменена.");
            } else if (dataStr.startsWith(CB_CANCEL_BOOKING)) {
                Long bookingId = parseLong(dataStr.substring(CB_CANCEL_BOOKING.length()));
                if (bookingId != null) onCancelBooking(tenant, chatId, bookingId);
            }
        } catch (NotFoundException | IllegalOperationException e) {
            telegramSender.sendMessage(tenant.getId(), chatId, "⚠️ " + e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка обработки callback для тенанта {}: {}", tenant.getSlug(), e.getMessage(), e);
            telegramSender.sendMessage(tenant.getId(), chatId,
                    "Что-то пошло не так. Попробуйте ещё раз.");
        }

        telegramSender.answerCallbackQuery(tenant.getId(), callbackId, null);
    }

    // ─── Flow steps ───────────────────────────────────────────────────────────

    private void sendWelcome(Tenant tenant, Long chatId) {
        String text = String.format(
                "👋 Здравствуйте! Это бот <b>%s</b>.\n\n" +
                        "Я помогу записаться на мероприятие.",
                escape(tenant.getName()));

        List<List<Map<String, Object>>> keyboard = new ArrayList<>();
        keyboard.add(List.of(button("📝 Записаться", CB_BOOK_START)));
        keyboard.add(List.of(button("📋 Мои записи", CB_MY_BOOKINGS)));

        telegramSender.sendMessage(tenant.getId(), chatId, text, keyboard);
    }

    private void sendHelp(Tenant tenant, Long chatId) {
        String text = "<b>Доступные команды:</b>\n" +
                "• /start — главное меню\n" +
                "• /book — записаться на мероприятие\n" +
                "• /mybookings — мои записи\n" +
                "• /cancel — отменить текущее действие\n" +
                "• /help — справка";
        telegramSender.sendMessage(tenant.getId(), chatId, text);
    }

    private void sendDefault(Tenant tenant, Long chatId) {
        telegramSender.sendMessage(tenant.getId(), chatId,
                "Я понимаю команды /start, /book, /mybookings, /help.");
    }

    private void startBookingFlow(Tenant tenant, Long chatId, BotDialogState state) {
        List<BotCategoryResponse> categories = botBookingService.getCategories(tenant.getSlug());
        if (categories.isEmpty()) {
            telegramSender.sendMessage(tenant.getId(), chatId,
                    "Пока нет доступных направлений для записи.");
            return;
        }

        state.reset();
        state.setStep(BotDialogStep.CHOOSING_CATEGORY);

        List<List<Map<String, Object>>> keyboard = new ArrayList<>();
        for (BotCategoryResponse c : categories) {
            keyboard.add(List.of(button(c.getName(), CB_CATEGORY + c.getId())));
        }
        keyboard.add(List.of(button("❌ Отмена", CB_CANCEL_FLOW)));

        telegramSender.sendMessage(tenant.getId(), chatId,
                "Выберите направление:", keyboard);
    }

    private void onCategorySelected(Tenant tenant, Long chatId, BotDialogState state, Long categoryId) {
        List<BotEventResponse> events = botBookingService.getEventsByCategory(tenant.getSlug(), categoryId);
        if (events.isEmpty()) {
            telegramSender.sendMessage(tenant.getId(), chatId,
                    "В этом направлении пока нет предстоящих событий.");
            return;
        }

        state.setSelectedCategoryId(categoryId);
        state.setStep(BotDialogStep.CHOOSING_EVENT);

        List<List<Map<String, Object>>> keyboard = new ArrayList<>();
        for (BotEventResponse e : events) {
            String label = e.getTitle();
            if (e.getStartDate() != null) {
                label += " • " + e.getStartDate();
            }
            keyboard.add(List.of(button(label, CB_EVENT + e.getId())));
        }
        keyboard.add(List.of(button("❌ Отмена", CB_CANCEL_FLOW)));

        telegramSender.sendMessage(tenant.getId(), chatId,
                "Выберите событие:", keyboard);
    }

    private void onEventSelected(Tenant tenant, Long chatId, BotDialogState state, Long eventId) {
        BotEventResponse event = botBookingService.getEventById(tenant.getSlug(), eventId);

        state.setSelectedEventId(eventId);
        state.setStep(BotDialogStep.ENTERING_NAME);

        StringBuilder sb = new StringBuilder();
        sb.append("<b>").append(escape(event.getTitle())).append("</b>\n");
        if (event.getStartDate() != null) {
            sb.append("📅 ").append(event.getStartDate());
            if (event.getEndDate() != null && !event.getEndDate().equals(event.getStartDate())) {
                sb.append(" — ").append(event.getEndDate());
            }
            sb.append("\n");
        }
        if (event.getPrice() != null && event.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            sb.append("💰 ").append(event.getPrice()).append(" ₽\n");
        }
        if (event.getAvailableSpots() != null) {
            sb.append("👥 Свободных мест: ").append(event.getAvailableSpots()).append("\n");
        }
        sb.append("\nКак вас зовут?");

        telegramSender.sendMessage(tenant.getId(), chatId, sb.toString());
    }

    private void sendConfirmation(Tenant tenant, Long chatId, BotDialogState state) {
        BotEventResponse event = botBookingService.getEventById(tenant.getSlug(), state.getSelectedEventId());

        String text = String.format(
                "<b>Подтвердите запись</b>\n\n" +
                        "Событие: <b>%s</b>\n" +
                        "Имя: %s\n" +
                        "Телефон: %s",
                escape(event.getTitle()),
                escape(state.getGuestName()),
                escape(state.getGuestPhone()));

        List<List<Map<String, Object>>> keyboard = List.of(
                List.of(button("✅ Подтвердить", CB_CONFIRM)),
                List.of(button("❌ Отмена", CB_CANCEL_FLOW))
        );

        telegramSender.sendMessage(tenant.getId(), chatId, text, keyboard);
    }

    private void onConfirm(Tenant tenant, Long chatId, BotDialogState state) {
        if (state.getStep() != BotDialogStep.CONFIRMING
                || state.getSelectedEventId() == null
                || state.getGuestName() == null
                || state.getGuestPhone() == null) {
            telegramSender.sendMessage(tenant.getId(), chatId,
                    "Нет активной записи для подтверждения. Начните заново с /book.");
            return;
        }

        BotBookingRequest request = new BotBookingRequest();
        request.setTenantSlug(tenant.getSlug());
        request.setEventId(state.getSelectedEventId());
        request.setTelegramId(chatId);
        request.setGuestName(state.getGuestName());
        request.setGuestPhone(state.getGuestPhone());

        BotBookingResponse booking = botBookingService.createBooking(request);

        state.reset();

        StringBuilder sb = new StringBuilder();
        sb.append("✅ <b>Вы записаны!</b>\n\n");
        sb.append("Событие: ").append(escape(booking.getEventTitle())).append("\n");
        if (booking.getAmountDue() != null && booking.getAmountDue().compareTo(BigDecimal.ZERO) > 0) {
            sb.append("К оплате: ").append(booking.getAmountDue()).append(" ₽\n");
        }
        sb.append("\nПосмотреть все записи: /mybookings");

        telegramSender.sendMessage(tenant.getId(), chatId, sb.toString());
    }

    // ─── Deep link: привязка чата к существующему бронированию ────────────────

    private void handleBookingDeepLink(Tenant tenant, Long chatId, String bookingIdStr) {
        Long bookingId = parseLong(bookingIdStr);
        if (bookingId == null) {
            sendWelcome(tenant, chatId);
            return;
        }

        try {
            BotBookingResponse booking = botBookingService.bindTelegramChat(
                    tenant.getSlug(), bookingId, chatId);

            StringBuilder sb = new StringBuilder();
            sb.append("✅ Чат привязан к вашей записи!\n\n");
            sb.append("Событие: <b>").append(escape(booking.getEventTitle())).append("</b>\n");
            if (booking.getStartDate() != null) {
                sb.append("📅 ").append(booking.getStartDate()).append("\n");
            }
            sb.append("Статус: ").append(statusLabel(booking.getStatus())).append("\n\n");
            sb.append("Теперь вы будете получать уведомления о статусе записи. " +
                    "Посмотреть все записи: /mybookings");

            telegramSender.sendMessage(tenant.getId(), chatId, sb.toString());
        } catch (NotFoundException | IllegalOperationException e) {
            telegramSender.sendMessage(tenant.getId(), chatId, "⚠️ " + e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка привязки чата к бронированию {}: {}", bookingId, e.getMessage(), e);
            telegramSender.sendMessage(tenant.getId(), chatId,
                    "Не удалось привязать чат к записи. Попробуйте позже.");
        }
    }

    // ─── My bookings / cancel ─────────────────────────────────────────────────

    private void sendMyBookings(Tenant tenant, Long chatId) {
        List<BotBookingResponse> bookings = botBookingService.getMyBookings(tenant.getSlug(), chatId);
        if (bookings.isEmpty()) {
            telegramSender.sendMessage(tenant.getId(), chatId,
                    "У вас нет активных записей. Запишитесь командой /book.");
            return;
        }

        for (BotBookingResponse b : bookings) {
            StringBuilder sb = new StringBuilder();
            sb.append("• <b>").append(escape(b.getEventTitle())).append("</b>\n");
            if (b.getStartDate() != null) {
                sb.append("  📅 ").append(b.getStartDate()).append("\n");
            }
            sb.append("  Статус: ").append(statusLabel(b.getStatus())).append("\n");
            if (b.getAmountDue() != null && b.getAmountDue().compareTo(BigDecimal.ZERO) > 0) {
                sb.append("  💰 ").append(b.getAmountDue()).append(" ₽ (")
                        .append(paymentLabel(b.getPaymentStatus())).append(")\n");
            }

            List<List<Map<String, Object>>> keyboard = List.of(
                    List.of(button("❌ Отменить", CB_CANCEL_BOOKING + b.getId()))
            );

            telegramSender.sendMessage(tenant.getId(), chatId, sb.toString(), keyboard);
        }
    }

    private void onCancelBooking(Tenant tenant, Long chatId, Long bookingId) {
        BotBookingResponse cancelled = botBookingService.cancelBooking(bookingId, chatId);
        telegramSender.sendMessage(tenant.getId(), chatId,
                String.format("Запись на <b>%s</b> отменена.",
                        escape(cancelled.getEventTitle())));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Map<String, Object> button(String text, String callbackData) {
        Map<String, Object> btn = new java.util.HashMap<>();
        btn.put("text", text);
        btn.put("callback_data", callbackData);
        return btn;
    }

    private String asString(Object obj) {
        return obj == null ? null : obj.toString();
    }

    private Long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String statusLabel(String status) {
        if (status == null) return "—";
        return switch (status) {
            case "PENDING" -> "ожидает подтверждения";
            case "CONFIRMED" -> "подтверждено";
            case "CANCELLED" -> "отменено";
            case "COMPLETED" -> "завершено";
            default -> status;
        };
    }

    private String paymentLabel(String status) {
        if (status == null) return "—";
        return switch (status) {
            case "PENDING" -> "ожидает оплаты";
            case "PAID" -> "оплачено";
            case "OVERDUE" -> "просрочено";
            case "NOT_REQUIRED" -> "не требуется";
            default -> status;
        };
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
