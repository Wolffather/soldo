package ru.savvy.soldo.season.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.event.dto.EventDTO;
import ru.savvy.soldo.season.dto.SeasonDTO;
import ru.savvy.soldo.shared.exception.EntityFinder;
import ru.savvy.soldo.shared.exception.NotFoundException;
import ru.savvy.soldo.event.mapper.EventMapper;
import ru.savvy.soldo.season.mapper.SeasonMapper;
import ru.savvy.soldo.season.model.Season;
import ru.savvy.soldo.event.model.EventStatus;
import ru.savvy.soldo.season.model.SeasonType;
import ru.savvy.soldo.booking.repository.EventBookingSummaryRepository;
import ru.savvy.soldo.event.repository.EventRepository;
import ru.savvy.soldo.season.repository.SeasonRepository;
import ru.savvy.soldo.season.service.SeasonService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeasonServiceImpl implements SeasonService {

    private final SeasonRepository seasonRepository;
    private final EventRepository eventRepository;
    private final EventBookingSummaryRepository summaryRepository;
    private final SeasonMapper seasonMapper;
    private final EventMapper eventMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SeasonDTO> getAll() {
        return seasonRepository.findAllByOrderByYearDescCreatedAtDesc()
                .stream()
                .map(this::buildSeasonDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeasonDTO> getUpcoming() {
        int currentYear = LocalDate.now().getYear();
        return seasonRepository.findUpcoming(EventStatus.PUBLISHED, currentYear, null)
                .stream()
                .map(this::buildSeasonDtoWithSessions)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeasonDTO> getUpcomingByPeriod(String period) {
        int currentYear = LocalDate.now().getYear();
        SeasonType seasonType = null;
        if (period != null && !period.isBlank()) {
            seasonType = SeasonType.valueOf(period.toUpperCase());
        }
        return seasonRepository.findUpcoming(EventStatus.PUBLISHED, currentYear, seasonType)
                .stream()
                .map(this::buildSeasonDtoWithSessions)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SeasonDTO getById(Long id) {
        Season season = EntityFinder.findOrThrow(seasonRepository.findById(id), "Сезон не найден: " + id);
        return buildSeasonDtoWithSessions(season);
    }

    @Override
    @Transactional
    public SeasonDTO create(SeasonDTO dto) {
        Season season = seasonMapper.dtoToEntity(dto);

        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            season.setStatus(EventStatus.valueOf(dto.getStatus()));
        } else {
            season.setStatus(EventStatus.PUBLISHED);
        }

        if (dto.getPeriod() != null && !dto.getPeriod().isBlank()) {
            season.setPeriod(SeasonType.valueOf(dto.getPeriod().toUpperCase()));
        }

        season = seasonRepository.save(season);
        return buildSeasonDto(season);
    }

    @Override
    @Transactional
    public SeasonDTO update(Long id, SeasonDTO dto) {
        Season season = EntityFinder.findOrThrow(seasonRepository.findById(id), "Сезон не найден: " + id);
        seasonMapper.updateEntityFromDto(dto, season);

        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            season.setStatus(EventStatus.valueOf(dto.getStatus()));
        }

        if (dto.getPeriod() != null && !dto.getPeriod().isBlank()) {
            season.setPeriod(SeasonType.valueOf(dto.getPeriod().toUpperCase()));
        } else {
            season.setPeriod(null);
        }

        season = seasonRepository.save(season);
        return buildSeasonDtoWithSessions(season);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!seasonRepository.existsById(id)) {
            throw new NotFoundException("Сезон не найден: " + id);
        }
        seasonRepository.deleteById(id);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /** Map season without sessions (for admin list). */
    private SeasonDTO buildSeasonDto(Season season) {
        return seasonMapper.entityToDto(season);
    }

    /** Map season with nested sessions and their available spots. */
    private SeasonDTO buildSeasonDtoWithSessions(Season season) {
        SeasonDTO dto = seasonMapper.entityToDto(season);

        List<EventDTO> sessions = eventRepository.findBySeasonId(season.getId())
                .stream()
                .map(event -> {
                    EventDTO eventDto = eventMapper.entityToDto(event);
                    summaryRepository.findByEventId(event.getId())
                            .ifPresent(s -> eventDto.setAvailableSpots(s.getNumOfParticipants()));
                    return eventDto;
                })
                .toList();

        dto.setSessions(sessions);
        return dto;
    }
}
