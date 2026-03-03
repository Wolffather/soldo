package ru.savvy.soldo.user.service;

import ru.savvy.soldo.user.dto.ParticipantProfileDTO;

import java.util.Optional;

public interface ParticipantProfileService {

    Optional<ParticipantProfileDTO> getByUserId(Long userId);

    ParticipantProfileDTO createOrUpdate(Long userId, ParticipantProfileDTO dto);
}