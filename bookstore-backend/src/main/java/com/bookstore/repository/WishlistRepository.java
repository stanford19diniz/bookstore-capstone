package com.bookstore.repository;

import com.bookstore.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
    Optional<Wishlist> findByUserIdAndIsDeletedFalse(UUID userId);
}
