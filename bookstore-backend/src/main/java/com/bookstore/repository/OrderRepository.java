package com.bookstore.repository;

import com.bookstore.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT o FROM Order o WHERE o.id = :id AND o.isDeleted = false")
    Optional<Order> findActiveById(@Param("id") UUID id);

    @Query("""
        SELECT o FROM Order o WHERE o.user.id = :userId
          AND o.isDeleted = false
          AND (:status IS NULL OR o.status = :status)
        """)
    Page<Order> findByUserIdAndStatus(
            @Param("userId") UUID userId,
            @Param("status") String status,
            Pageable pageable);

    @Query("""
        SELECT o FROM Order o WHERE o.isDeleted = false
          AND (:status IS NULL OR o.status = :status)
          AND (:userId IS NULL OR o.user.id = :userId)
        """)
    Page<Order> findAllWithFilters(
            @Param("status") String status,
            @Param("userId") UUID userId,
            Pageable pageable);
}
