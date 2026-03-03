package ru.savvy.soldo.event.service;

import ru.savvy.soldo.event.dto.EventCategoryDTO;
import ru.savvy.soldo.event.model.EventFormat;
import ru.savvy.soldo.season.model.SeasonType;

import java.util.List;

public interface EventCategoryService {

    List<EventCategoryDTO> getAll();

    EventCategoryDTO getById(Long id);

    List<EventCategoryDTO> getByFormat(EventFormat format);

    List<EventCategoryDTO> getBySeason(SeasonType season);

    EventCategoryDTO create(EventCategoryDTO dto);

    EventCategoryDTO update(Long id, EventCategoryDTO dto);

    void delete(Long id);
}