package ru.savvy.soldo.service;

import ru.savvy.soldo.dto.EventCategoryDTO;
import ru.savvy.soldo.model.enums.EventFormat;
import ru.savvy.soldo.model.enums.SeasonType;

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