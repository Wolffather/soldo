package ru.savvy.soldo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.dto.EventDTO;
import ru.savvy.soldo.model.Event;



public interface EventService {


    Page<EventDTO> getAll(Pageable pageable);

    EventDTO create(EventDTO dto);

    EventDTO getById(Long id);

    @Transactional
    EventDTO update(Long id, EventDTO dto);

    @Transactional
    void delete(Long id);
}
