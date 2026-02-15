package ru.savvy.soldo.service;

import ru.savvy.soldo.dto.ParticipantProfileDTO;

import java.util.Optional;

public interface ParticipantProfileService {

    Optional<ParticipantProfileDTO> getByUserId(Long userId);

    ParticipantProfileDTO createOrUpdate(Long userId, ParticipantProfileDTO dto);
}