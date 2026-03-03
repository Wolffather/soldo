package ru.savvy.soldo.review.service;

import ru.savvy.soldo.review.dto.ReviewDTO;

import java.util.List;

public interface ReviewService {

    List<ReviewDTO> getApproved();

    void submit(ReviewDTO dto);

    List<ReviewDTO> getAll();

    ReviewDTO approve(Long id);

    void delete(Long id);
}
