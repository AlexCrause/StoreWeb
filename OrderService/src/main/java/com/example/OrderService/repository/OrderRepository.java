package com.example.OrderService.repository;

import com.example.OrderService.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

//    // Проверка существования заказа по userId и статусу
//    boolean existsByUserIdAndStatus(UUID userId, String status);
//
//    // Поиск первого заказа по userId и статусу
//    Optional<Order> findFirstByUserIdAndStatus(UUID userId, String status);
//
//    // Дополнительный запрос, если требуется
//    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.status = :status")
//    Optional<Order> findOrderByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") String status);
    List<Order> findAllByProductId(UUID productId);
}
