package ru.savvy.soldo.event.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.event.dto.EventDTO;

import java.util.List;

public interface EventService {

    Page<EventDTO> getAll(Pageable pageable);

    List<EventDTO> getUpcoming(String season);

    EventDTO create(EventDTO dto);

    EventDTO getById(Long id);

    @Transactional
    EventDTO update(Long id, EventDTO dto);

    @Transactional
    void delete(Long id);
}
