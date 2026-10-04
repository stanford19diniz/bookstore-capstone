package com.bookstore.repository;

import com.bookstore.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId AND c.status = 'ACTIVE' AND c.isDeleted = false")
    Optional<Cart> findActiveCartByUserId(@Param("userId") UUID userId);
}
