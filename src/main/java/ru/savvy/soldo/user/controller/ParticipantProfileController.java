package ru.savvy.soldo.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.user.dto.ParticipantProfileDTO;
import ru.savvy.soldo.user.service.ParticipantProfileService;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ParticipantProfileController {

    private final ParticipantProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ParticipantProfileDTO> getMyProfile(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        return profileService.getByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<ParticipantProfileDTO> updateMyProfile(
            Authentication auth,
            @Valid @RequestBody ParticipantProfileDTO dto) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(profileService.createOrUpdate(userId, dto));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ParticipantProfileDTO> getByUserId(@PathVariable Long userId) {
        return profileService.getByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}