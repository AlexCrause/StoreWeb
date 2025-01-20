package com.example.reviewservice.repository;

import com.example.reviewservice.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findAllByProductIdAndApprovedTrue(UUID productId);
}
