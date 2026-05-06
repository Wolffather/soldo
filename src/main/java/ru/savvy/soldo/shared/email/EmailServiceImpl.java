package ru.savvy.soldo.shared.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.savvy.soldo.booking.model.Booking;
import ru.savvy.soldo.booking.model.BookingDocument;
import ru.savvy.soldo.document.model.DocumentTemplate;
import ru.savvy.soldo.shared.settings.AppSettingsService;

import java.util.List;
import java.util.Properties;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final AppSettingsService settings;

    @Override
    public void sendDocuments(Booking booking, List<BookingDocument> documents) {
        boolean enabled = Boolean.parseBoolean(settings.get("mail.enabled", "false"));
        if (!enabled) {
            log.info("Mail disabled — skipping document email for booking {}", booking.getId());
            return;
        }

        String email = booking.getGuestEmail();
        if (email == null || email.isBlank()) {
            log.warn("No guest email for booking {} — cannot send documents", booking.getId());
            return;
        }

        List<BookingDocument> withTemplate = documents.stream()
                .filter(d -> d.getDocumentTemplate() != null && d.getDocumentTemplate().getFileUrl() != null)
                .toList();

        if (withTemplate.isEmpty()) {
            log.info("No documents with files for booking {} — nothing to send", booking.getId());
            return;
        }

        String from = settings.get("mail.from", "noreply@example.com");
        String publicUrl = settings.get("app.public-url", "http://localhost:8080");

        try {
            JavaMailSenderImpl mailSender = buildMailSender();
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, false, "UTF-8");
            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject("Документы для участия: " + booking.getEvent().getTitle());
            helper.setText(buildHtml(booking, withTemplate, publicUrl), true);
            mailSender.send(msg);
            log.info("Document email sent to {} for booking {}", email, booking.getId());
        } catch (Exception e) {
            log.error("Failed to send document email for booking {}: {}", booking.getId(), e.getMessage());
        }
    }

    private JavaMailSenderImpl buildMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(settings.get("mail.host", "smtp.gmail.com"));
        sender.setPort(Integer.parseInt(settings.get("mail.port", "587")));
        sender.setUsername(settings.get("mail.username", ""));
        sender.setPassword(settings.get("mail.password", ""));

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", settings.get("mail.host", "smtp.gmail.com"));
        return sender;
    }

    private String buildHtml(Booking booking, List<BookingDocument> documents, String publicUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"font-family:sans-serif;max-width:600px;margin:0 auto;\">");
        sb.append("<h2 style=\"color:#2563eb;\">").append(escape(booking.getEvent().getTitle())).append("</h2>");
        sb.append("<p>Здравствуйте, <strong>").append(escape(booking.getGuestName())).append("</strong>!</p>");
        sb.append("<p>Ваше бронирование подтверждено. Пожалуйста, ознакомьтесь с документами ниже");
        sb.append(" и при необходимости подпишите их.</p>");
        sb.append("<ul style=\"padding-left:20px;\">");

        for (BookingDocument bd : documents) {
            DocumentTemplate t = bd.getDocumentTemplate();
            String fileLink = publicUrl + "/files/" + t.getFileUrl();
            sb.append("<li style=\"margin-bottom:8px;\">");
            sb.append("<a href=\"").append(fileLink).append("\" style=\"color:#2563eb;\">")
              .append(escape(t.getName())).append("</a>");
            if (Boolean.TRUE.equals(t.getRequiresSignature())) {
                sb.append(" <span style=\"color:#6b7280;font-size:0.875em;\">(требует подписи)</span>");
            }
            if (t.getDescription() != null && !t.getDescription().isBlank()) {
                sb.append("<br><span style=\"color:#6b7280;font-size:0.875em;\">")
                  .append(escape(t.getDescription())).append("</span>");
            }
            sb.append("</li>");
        }

        sb.append("</ul>");
        sb.append("<hr style=\"border:none;border-top:1px solid #e5e7eb;margin:24px 0;\">");
        sb.append("<p style=\"color:#6b7280;font-size:0.875em;\">Если у вас есть вопросы, ответьте на это письмо.</p>");
        sb.append("</div>");
        return sb.toString();
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
