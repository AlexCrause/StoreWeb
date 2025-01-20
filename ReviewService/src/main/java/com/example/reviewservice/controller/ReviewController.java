package com.example.reviewservice.controller;

import com.example.reviewservice.dto.ReviewCreateDTO;
import com.example.reviewservice.dto.ReviewResponseDTO;
import com.example.reviewservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

//    @PostMapping
//    public ResponseEntity<ReviewResponseDTO> createReview(@RequestBody ReviewCreateDTO reviewDTO) {
//        return ResponseEntity.ok(reviewService.createReview(reviewDTO));
//    }

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(@RequestBody ReviewCreateDTO reviewDTO) {
        try {
            ReviewResponseDTO response = reviewService.createReview(reviewDTO);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Ошибка при создании отзыва: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByProduct(@PathVariable UUID productId) {
        List<ReviewResponseDTO> approvedReviews = reviewService.getApprovedReviewsByProduct(productId);
        return ResponseEntity.ok(approvedReviews);
    }
}
