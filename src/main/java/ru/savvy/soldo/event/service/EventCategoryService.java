package ru.savvy.soldo.event.service;

import ru.savvy.soldo.event.dto.EventCategoryDTO;
import ru.savvy.soldo.event.model.EventFormat;

import java.util.List;

public interface EventCategoryService {

    List<EventCategoryDTO> getAll();

    EventCategoryDTO getById(Long id);

    List<EventCategoryDTO> getByFormat(EventFormat format);

    EventCategoryDTO create(EventCategoryDTO dto);

    EventCategoryDTO update(Long id, EventCategoryDTO dto);

    void delete(Long id);
}
