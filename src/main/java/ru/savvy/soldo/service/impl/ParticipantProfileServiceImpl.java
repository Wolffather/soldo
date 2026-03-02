package ru.savvy.soldo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.dto.ParticipantProfileDTO;
import ru.savvy.soldo.exception.NotFoundException;
import ru.savvy.soldo.model.ParticipantProfile;
import ru.savvy.soldo.model.User;
import ru.savvy.soldo.repository.ParticipantProfileRepository;
import ru.savvy.soldo.repository.UserRepository;
import ru.savvy.soldo.service.ParticipantProfileService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipantProfileServiceImpl implements ParticipantProfileService {

    private final ParticipantProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Override
    public Optional<ParticipantProfileDTO> getByUserId(Long userId) {
        return profileRepository.findByUserId(userId).map(this::toDTO);
    }

    @Override
    @Transactional
    public ParticipantProfileDTO createOrUpdate(Long userId, ParticipantProfileDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));

        ParticipantProfile profile = profileRepository.findByUserId(userId)
                .orElse(ParticipantProfile.builder().user(user).build());

        profile.setFullName(dto.getFullName());
        profile.setBirthDate(dto.getBirthDate());
        profile.setMedicalNotes(dto.getMedicalNotes());
        profile.setParentFullName(dto.getParentFullName());
        profile.setParentPhone(dto.getParentPhone());
        profile.setParentEmail(dto.getParentEmail());

        if (dto.getConsentPersonalData() != null && dto.getConsentPersonalData()) {
            profile.setConsentPersonalData(true);
            profile.setConsentDate(LocalDateTime.now());
        }
        if (dto.getConsentPhotoVideo() != null) {
            profile.setConsentPhotoVideo(dto.getConsentPhotoVideo());
        }

        return toDTO(profileRepository.save(profile));
    }

    private ParticipantProfileDTO toDTO(ParticipantProfile p) {
        ParticipantProfileDTO dto = new ParticipantProfileDTO();
        dto.setId(p.getId());
        dto.setUserId(p.getUser().getId());
        dto.setFullName(p.getFullName());
        dto.setBirthDate(p.getBirthDate());
        dto.setMedicalNotes(p.getMedicalNotes());
        dto.setParentFullName(p.getParentFullName());
        dto.setParentPhone(p.getParentPhone());
        dto.setParentEmail(p.getParentEmail());
        dto.setConsentPersonalData(p.getConsentPersonalData());
        dto.setConsentPhotoVideo(p.getConsentPhotoVideo());
        return dto;
    }
}
