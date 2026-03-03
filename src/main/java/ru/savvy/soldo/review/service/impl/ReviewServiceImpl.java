package ru.savvy.soldo.review.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.savvy.soldo.review.dto.ReviewDTO;
import ru.savvy.soldo.shared.exception.EntityFinder;
import ru.savvy.soldo.review.mapper.ReviewMapper;
import ru.savvy.soldo.review.model.Review;
import ru.savvy.soldo.review.repository.ReviewRepository;
import ru.savvy.soldo.review.service.ReviewService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public List<ReviewDTO> getApproved() {
        return reviewRepository.findByApprovedTrueOrderByCreatedAtDesc()
                .stream()
                .map(reviewMapper::entityToDto)
                .toList();
    }

    @Override
    public void submit(ReviewDTO dto) {
        reviewRepository.save(reviewMapper.dtoToEntity(dto));
    }

    @Override
    public List<ReviewDTO> getAll() {
        return reviewRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(reviewMapper::entityToDto)
                .toList();
    }

    @Override
    public ReviewDTO approve(Long id) {
        Review review = EntityFinder.findOrThrow(reviewRepository.findById(id), "Отзыв не найден: " + id);
        review.setApproved(true);
        return reviewMapper.entityToDto(reviewRepository.save(review));
    }

    @Override
    public void delete(Long id) {
        EntityFinder.findOrThrow(reviewRepository.findById(id), "Отзыв не найден: " + id);
        reviewRepository.deleteById(id);
    }
}
