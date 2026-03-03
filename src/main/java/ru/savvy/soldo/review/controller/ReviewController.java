package ru.savvy.soldo.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.review.dto.ReviewDTO;
import ru.savvy.soldo.review.service.ReviewService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /** Public: list approved reviews */
    @GetMapping("/public/reviews")
    public ResponseEntity<List<ReviewDTO>> getApproved() {
        return ResponseEntity.ok(reviewService.getApproved());
    }

    /** Public: submit a new review (pending approval) */
    @PostMapping("/public/reviews")
    public ResponseEntity<Void> submit(@Valid @RequestBody ReviewDTO dto) {
        reviewService.submit(dto);
        return ResponseEntity.ok().build();
    }

    /** Admin: list all reviews */
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewDTO>> getAll() {
        return ResponseEntity.ok(reviewService.getAll());
    }

    /** Admin: approve a review */
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @PatchMapping("/reviews/{id}/approve")
    public ResponseEntity<ReviewDTO> approve(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.approve(id));
    }

    /** Admin: delete a review */
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
