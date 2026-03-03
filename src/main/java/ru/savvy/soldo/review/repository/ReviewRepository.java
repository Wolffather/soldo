package ru.savvy.soldo.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.review.model.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByOrderByCreatedAtDesc();

    List<Review> findByApprovedTrueOrderByCreatedAtDesc();
}
