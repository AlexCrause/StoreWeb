package com.example.reviewservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ReviewResponseDTO {
    private UUID id;
    private UUID productId;
    private UUID userId;
    private String author;
    private String content;
    private int rating;
    private boolean approved;
    private long createdAt;
    private long updatedAt;
}
