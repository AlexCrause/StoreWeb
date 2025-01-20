package com.example.reviewservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Builder
@Entity
@Data
@Table(name = "reviews")
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    private UUID productId; // ID товара, к которому относится отзыв

    private UUID userId; // ID пользователя, оставившего отзыв

    private String author;

    @Column(nullable = false)
    private String content; // Текст отзыва

    @Column(nullable = false)
    private int rating; // Оценка (например, от 1 до 5)

    @Column(nullable = false)
    private boolean approved; // Статус модерации

    @Column(nullable = false, updatable = false)
    private long createdAt; // Время создания

    private long updatedAt;

    public Review() {
    }

    // Время обновления
}
