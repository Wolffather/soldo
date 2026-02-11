package ru.savvy.soldo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<List<Booking>> findAllByUserId(Long userId);

}
