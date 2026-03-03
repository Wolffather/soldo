package ru.savvy.soldo.season.service;

import ru.savvy.soldo.season.dto.SeasonDTO;

import java.util.List;

public interface SeasonService {

    List<SeasonDTO> getAll();

    List<SeasonDTO> getUpcoming();

    List<SeasonDTO> getUpcomingByPeriod(String period);

    SeasonDTO getById(Long id);

    SeasonDTO create(SeasonDTO dto);

    SeasonDTO update(Long id, SeasonDTO dto);

    void delete(Long id);
}
