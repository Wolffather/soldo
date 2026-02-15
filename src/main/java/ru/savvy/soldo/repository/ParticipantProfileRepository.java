package ru.savvy.soldo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.model.ParticipantProfile;

import java.util.Optional;

public interface ParticipantProfileRepository extends JpaRepository<ParticipantProfile, Long> {

    Optional<ParticipantProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}