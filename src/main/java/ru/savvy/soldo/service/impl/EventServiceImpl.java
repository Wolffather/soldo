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
import ru.savvy.soldo.model.enums.AgeGroup;
import ru.savvy.soldo.model.enums.EventStatus;
import ru.savvy.soldo.repository.EventBookingSummaryRepository;
import ru.savvy.soldo.repository.EventCategoryRepository;
import ru.savvy.soldo.repository.EventRepository;
import ru.savvy.soldo.service.EventService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventBookingSummaryRepository summaryRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final EventMapper mapper;

    @Override
    @Transactional
    public EventDTO create(EventDTO dto) {
        Event event = mapper.dtoToEntity(dto);

        if (dto.getCategoryName() != null && !dto.getCategoryName().isBlank()) {
            EventCategory category = eventCategoryRepository.findByName(dto.getCategoryName())
                    .orElseThrow(() -> new NotFoundException(
                            "Категория не найдена: " + dto.getCategoryName()));
            event.setCategory(category);
        }

        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            event.setStatus(EventStatus.valueOf(dto.getStatus()));
        } else {
            event.setStatus(EventStatus.PUBLISHED);
        }

        if (dto.getAgeGroup() != null && !dto.getAgeGroup().isBlank()) {
            event.setAgeGroup(AgeGroup.valueOf(dto.getAgeGroup()));
        }

        event = eventRepository.save(event);
        createSummary(event);

        return mapper.entityToDto(event);
    }

    @Override
    public Page<EventDTO> getAll(Pageable pageable) {
        return eventRepository.findAllWithCategory(pageable)
                .map(mapper::entityToDto);
    }

    @Override
    public EventDTO getById(Long id) {
        Event event = eventRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: " + id));
        return mapper.entityToDto(event);
    }

    @Override
    @Transactional
    public EventDTO update(Long id, EventDTO dto) {
        Event event = eventRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: " + id));

        mapper.updateEntityFromDto(dto, event);

        if (dto.getCategoryName() != null && !dto.getCategoryName().isBlank()) {
            EventCategory category = eventCategoryRepository.findByName(dto.getCategoryName())
                    .orElseThrow(() -> new NotFoundException(
                            "Категория не найдена: " + dto.getCategoryName()));
            event.setCategory(category);
        } else {
            event.setCategory(null);
        }

        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            event.setStatus(EventStatus.valueOf(dto.getStatus()));
        }

        if (dto.getAgeGroup() != null && !dto.getAgeGroup().isBlank()) {
            event.setAgeGroup(AgeGroup.valueOf(dto.getAgeGroup()));
        }

        event = eventRepository.save(event);
        updateSummary(event);

        return mapper.entityToDto(event);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException("Событие не найдено: " + id);
        }
        summaryRepository.deleteById(id);
        eventRepository.deleteById(id);
    }

    private void createSummary(Event event) {
        EventBookingsSummary summary = new EventBookingsSummary();
        summary.setEvent(event);
        summary.setTotalBookings(0);
        summary.setConfirmedBookings(0);
        summary.setPendingBookings(0L);
        summary.setCancelledBookings(0L);
        summary.setNumOfParticipants(event.getMaxParticipants());
        summaryRepository.save(summary);
    }

    private void updateSummary(Event event) {
        summaryRepository.findByEventId(event.getId()).ifPresent(summary -> {
            summary.getEvent().setTitle(event.getTitle());
            long occupied = summary.getConfirmedBookings() + summary.getPendingBookings();
            summary.setNumOfParticipants((int) (event.getMaxParticipants() - occupied));
            summaryRepository.save(summary);
        });
    }

}