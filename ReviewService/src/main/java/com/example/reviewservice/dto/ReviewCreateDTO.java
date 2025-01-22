package com.example.reviewservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ReviewCreateDTO {
    private UUID productId;
    private UUID userId;
    private String author;
    private String content;
    private int rating;
}
