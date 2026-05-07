package ru.savvy.soldo.event.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.booking.repository.BookingRepository;
import ru.savvy.soldo.event.dto.EventDTO;
import ru.savvy.soldo.event.dto.EventPriceOptionDTO;
import ru.savvy.soldo.shared.exception.ForbiddenOperationException;
import ru.savvy.soldo.shared.exception.NotFoundException;
import ru.savvy.soldo.event.mapper.EventMapper;
import ru.savvy.soldo.event.model.Event;
import ru.savvy.soldo.event.model.EventPriceOption;
import ru.savvy.soldo.booking.model.EventBookingsSummary;
import ru.savvy.soldo.event.model.EventStatus;
import ru.savvy.soldo.booking.repository.EventBookingSummaryRepository;
import ru.savvy.soldo.event.repository.EventPriceOptionRepository;
import ru.savvy.soldo.event.repository.EventRepository;
import ru.savvy.soldo.event.service.EventService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventBookingSummaryRepository summaryRepository;
    private final EventPriceOptionRepository priceOptionRepository;
    private final BookingRepository bookingRepository;
    private final EventMapper mapper;

    @Override
    @Transactional
    public EventDTO create(EventDTO dto) {
        Event event = mapper.dtoToEntity(dto);

        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            event.setStatus(EventStatus.valueOf(dto.getStatus()));
        } else {
            event.setStatus(EventStatus.PUBLISHED);
        }

        event = eventRepository.save(event);
        savePriceOptions(event, dto.getPriceOptions());

        if (event.getMaxParticipants() != null) {
            createSummary(event);
        }

        return buildEventDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDTO> getUpcoming(String season) {
        LocalDate today = LocalDate.now();
        return eventRepository.findUpcoming(today).stream()
                .map(this::buildEventDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDTO> getAll(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(this::buildEventDto);
    }

    @Override
    @Transactional(readOnly = true)
    public EventDTO getById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: " + id));
        return buildEventDto(event);
    }

    @Override
    @Transactional
    public EventDTO update(Long id, EventDTO dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: " + id));

        mapper.updateEntityFromDto(dto, event);

        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            event.setStatus(EventStatus.valueOf(dto.getStatus()));
        }

        event = eventRepository.save(event);
        savePriceOptions(event, dto.getPriceOptions());

        if (event.getMaxParticipants() != null) {
            updateSummary(event);
        }

        return buildEventDto(event);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException("Событие не найдено: " + id);
        }
        long activeBookings = bookingRepository.countByEventIdAndCancelledFalse(id);
        if (activeBookings > 0) {
            throw new ForbiddenOperationException(
                "Невозможно удалить событие: к нему привязано " + activeBookings +
                " активных бронирований. Сначала отмените все бронирования.");
        }
        summaryRepository.findByEventId(id).ifPresent(summaryRepository::delete);
        priceOptionRepository.deleteByEventId(id);
        eventRepository.deleteById(id);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void savePriceOptions(Event event, List<EventPriceOptionDTO> options) {
        if (options == null) return;
        priceOptionRepository.deleteByEventId(event.getId());
        for (int i = 0; i < options.size(); i++) {
            EventPriceOptionDTO opt = options.get(i);
            if (opt.getName() == null || opt.getName().isBlank() || opt.getPrice() == null) continue;
            priceOptionRepository.save(EventPriceOption.builder()
                    .event(event)
                    .name(opt.getName().trim())
                    .price(opt.getPrice())
                    .sortOrder(i)
                    .build());
        }
    }

    private EventDTO buildEventDto(Event event) {
        EventDTO dto = mapper.entityToDto(event);

        if (event.getMaxParticipants() != null) {
            summaryRepository.findByEventId(event.getId())
                    .ifPresent(s -> dto.setAvailableSpots(s.getNumOfParticipants()));
        }

        List<EventPriceOption> options = priceOptionRepository
                .findByEventIdOrderBySortOrderAscIdAsc(event.getId());
        dto.setPriceOptions(options.stream()
                .map(o -> EventPriceOptionDTO.builder()
                        .id(o.getId())
                        .name(o.getName())
                        .price(o.getPrice())
                        .build())
                .toList());

        return dto;
    }

    private void createSummary(Event event) {
        summaryRepository.save(EventBookingsSummary.of(event));
    }

    private void updateSummary(Event event) {
        if (event.getMaxParticipants() != null) {
            summaryRepository.findByEventId(event.getId())
                    .ifPresent(s -> summaryRepository.refreshSummary(event.getId()));
        }
    }
}
