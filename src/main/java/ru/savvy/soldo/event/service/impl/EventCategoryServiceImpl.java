package ru.savvy.soldo.event.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.savvy.soldo.event.dto.EventCategoryDTO;
import ru.savvy.soldo.shared.exception.EntityFinder;
import ru.savvy.soldo.event.model.EventCategory;
import ru.savvy.soldo.event.model.EventFormat;
import ru.savvy.soldo.season.model.SeasonType;
import ru.savvy.soldo.event.repository.EventCategoryRepository;
import ru.savvy.soldo.event.service.EventCategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventCategoryServiceImpl implements EventCategoryService {

    private final EventCategoryRepository categoryRepository;

    @Override
    public List<EventCategoryDTO> getAll() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public EventCategoryDTO getById(Long id) {
        return toDTO(EntityFinder.findOrThrow(categoryRepository.findById(id), "Категория не найдена: " + id));
    }

    @Override
    public List<EventCategoryDTO> getByFormat(EventFormat format) {
        return categoryRepository.findByFormat(format).stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public List<EventCategoryDTO> getBySeason(SeasonType season) {
        return categoryRepository.findBySeason(season).stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public EventCategoryDTO create(EventCategoryDTO dto) {
        EventCategory category = EventCategory.builder()
                .name(dto.getName())
                .format(EventFormat.valueOf(dto.getFormat()))
                .season(dto.getSeason() != null ? SeasonType.valueOf(dto.getSeason()) : null)
                .description(dto.getDescription())
                .iconUrl(dto.getIconUrl())
                .build();
        return toDTO(categoryRepository.save(category));
    }

    @Override
    public EventCategoryDTO update(Long id, EventCategoryDTO dto) {
        EventCategory category = EntityFinder.findOrThrow(categoryRepository.findById(id), "Категория не найдена: " + id);
        category.setName(dto.getName());
        category.setFormat(EventFormat.valueOf(dto.getFormat()));
        category.setSeason(dto.getSeason() != null ? SeasonType.valueOf(dto.getSeason()) : null);
        category.setDescription(dto.getDescription());
        category.setIconUrl(dto.getIconUrl());
        return toDTO(categoryRepository.save(category));
    }

    @Override
    public void delete(Long id) {
        EntityFinder.findOrThrow(categoryRepository.findById(id), "Категория не найдена: " + id);
        categoryRepository.deleteById(id);
    }

    private EventCategoryDTO toDTO(EventCategory c) {
        return EventCategoryDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .format(c.getFormat().name())
                .season(c.getSeason() != null ? c.getSeason().name() : null)
                .description(c.getDescription())
                .iconUrl(c.getIconUrl())
                .build();
    }
}