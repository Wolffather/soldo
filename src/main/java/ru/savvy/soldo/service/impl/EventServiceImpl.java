package ru.savvy.soldo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.dto.EventDTO;
import ru.savvy.soldo.exception.NotFoundException;
import ru.savvy.soldo.mapper.EventMapper;
import ru.savvy.soldo.model.Event;
import ru.savvy.soldo.model.EventBookingsSummary;
import ru.savvy.soldo.model.EventCategory;
import ru.savvy.soldo.model.Season;
import ru.savvy.soldo.model.enums.EventFormat;
import ru.savvy.soldo.model.enums.EventStatus;
import ru.savvy.soldo.model.enums.SeasonType;
import ru.savvy.soldo.repository.EventBookingSummaryRepository;
import ru.savvy.soldo.repository.EventCategoryRepository;
import ru.savvy.soldo.repository.EventRepository;
import ru.savvy.soldo.repository.SeasonRepository;
import ru.savvy.soldo.service.EventService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventBookingSummaryRepository summaryRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final SeasonRepository seasonRepository;
    private final EventMapper mapper;

    @Override
    @Transactional
    public EventDTO create(EventDTO dto) {
        Event event = mapper.dtoToEntity(dto);

        // Resolve category
        EventCategory category = resolveCategory(dto, event);

        // Set status
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            event.setStatus(EventStatus.valueOf(dto.getStatus()));
        } else {
            event.setStatus(EventStatus.PUBLISHED);
        }

        // Resolve season — required for SESSION_* formats
        resolveSeason(dto, event, category);

        // Validate session fields if linked to a season
        if (event.getSeason() != null) {
            validateSession(event, event.getSeason(), null);
        }

        event = eventRepository.save(event);

        // Create summary for events with capacity
        if (event.getMaxParticipants() != null) {
            createSummary(event);
        }

        return mapper.entityToDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDTO> getUpcoming(String season) {
        LocalDate today = LocalDate.now();

        List<Event> events;
        if ("ALL_YEAR".equalsIgnoreCase(season)) {
            events = eventRepository.findUpcomingYearRound(today);
        } else {
            SeasonType seasonType = SeasonType.valueOf(season.toUpperCase());
            events = eventRepository.findUpcomingBySeason(seasonType, today);
        }

        return events.stream().map(this::buildEventDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDTO> getAll(Pageable pageable) {
        return eventRepository.findAllWithCategory(pageable)
                .map(mapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public EventDTO getById(Long id) {
        Event event = eventRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: " + id));
        return buildEventDto(event);
    }

    @Override
    @Transactional
    public EventDTO update(Long id, EventDTO dto) {
        Event event = eventRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: " + id));

        mapper.updateEntityFromDto(dto, event);

        // Resolve category
        EventCategory category = resolveCategory(dto, event);

        // Set status
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            event.setStatus(EventStatus.valueOf(dto.getStatus()));
        }

        // Resolve season
        resolveSeason(dto, event, category);

        // Validate session fields if linked to a season
        if (event.getSeason() != null) {
            validateSession(event, event.getSeason(), id);
        }

        event = eventRepository.save(event);

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
        summaryRepository.findByEventId(id).ifPresent(summaryRepository::delete);
        eventRepository.deleteById(id);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private EventCategory resolveCategory(EventDTO dto, Event event) {
        if (dto.getCategoryName() != null && !dto.getCategoryName().isBlank()) {
            EventCategory category = eventCategoryRepository.findByName(dto.getCategoryName())
                    .orElseThrow(() -> new NotFoundException(
                            "Категория не найдена: " + dto.getCategoryName()));
            event.setCategory(category);
            return category;
        } else {
            event.setCategory(null);
            return null;
        }
    }

    private void resolveSeason(EventDTO dto, Event event, EventCategory category) {
        boolean isSessionFormat = category != null
                && EventFormat.SESSION_FORMATS.contains(category.getFormat());

        if (dto.getSeasonId() != null) {
            Season season = seasonRepository.findById(dto.getSeasonId())
                    .orElseThrow(() -> new NotFoundException(
                            "Сезон не найден: " + dto.getSeasonId()));
            event.setSeason(season);
        } else if (isSessionFormat) {
            throw new IllegalArgumentException(
                    "Для события типа «" + category.getName() + "» необходимо указать сезон");
        } else {
            event.setSeason(null);
        }
    }

    private EventDTO buildEventDto(Event event) {
        EventDTO dto = mapper.entityToDto(event);

        if (event.getMaxParticipants() != null) {
            summaryRepository.findByEventId(event.getId())
                    .ifPresent(s -> dto.setAvailableSpots(s.getNumOfParticipants()));
        }

        return dto;
    }

    /**
     * Validate that a session's dates do not overlap with siblings in the same season.
     */
    private void validateSession(Event session, Season season, Long excludeId) {
        LocalDate start = session.getStartDate();
        LocalDate end = session.getEndDate();

        if (start == null) {
            throw new IllegalArgumentException("Дата начала обязательна для смены");
        }
        if (end == null) {
            throw new IllegalArgumentException("Дата окончания обязательна для смены");
        }

        // Validate dates fall within the season's year
        if (start.getYear() != season.getYear()) {
            throw new IllegalArgumentException(
                    "Дата начала смены должна быть в " + season.getYear() + " году");
        }
        if (end.getYear() != season.getYear()) {
            throw new IllegalArgumentException(
                    "Дата окончания смены должна быть в " + season.getYear() + " году");
        }

        // Validate no overlap with sibling sessions
        if (season.getId() != null) {
            boolean overlaps = eventRepository.hasOverlappingSession(
                    season.getId(), start, end, excludeId);
            if (overlaps) {
                throw new IllegalArgumentException(
                        "Даты смены пересекаются с другой сменой этого сезона");
            }
        }
    }

    private void createSummary(Event event) {
        summaryRepository.save(EventBookingsSummary.of(event));
    }

    private void updateSummary(Event event) {
        summaryRepository.findByEventId(event.getId()).ifPresent(summary -> {
            if (event.getMaxParticipants() != null) {
                long occupied = summary.getConfirmedBookings() + summary.getPendingBookings();
                summary.setNumOfParticipants((int) (event.getMaxParticipants() - occupied));
                summaryRepository.save(summary);
            }
        });
    }
}
