package com.example.reviewservice.service;

import com.example.reviewservice.dto.ReviewCreateDTO;
import com.example.reviewservice.dto.ReviewResponseDTO;
import com.example.reviewservice.model.Review;
import com.example.reviewservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewResponseDTO createReview(ReviewCreateDTO reviewDTO) {
        Review review = Review.builder()
                .productId(reviewDTO.getProductId())
                .userId(reviewDTO.getUserId())
                .author(reviewDTO.getAuthor())
                .content(reviewDTO.getContent())
                .rating(reviewDTO.getRating())
                .approved(true)
                .createdAt(Instant.now().toEpochMilli())
                .updatedAt(Instant.now().toEpochMilli())
                .build();

        review = reviewRepository.save(review);

        return mapToResponseDTO(review);
    }

    public List<ReviewResponseDTO> getApprovedReviewsByProduct(UUID productId) {
        return reviewRepository.findAllByProductIdAndApprovedTrue(productId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private ReviewResponseDTO mapToResponseDTO(Review review) {
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .productId(review.getProductId())
                .userId(review.getUserId())
                .author(review.getAuthor())
                .content(review.getContent())
                .rating(review.getRating())
                .approved(review.isApproved())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
