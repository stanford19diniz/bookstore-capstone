package com.bookstore.repository;

import com.bookstore.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findByBookIdAndStatusAndIsDeletedFalse(UUID bookId, String status, Pageable pageable);

    Page<Review> findByUserIdAndIsDeletedFalse(UUID userId, Pageable pageable);

    boolean existsByUserIdAndBookIdAndIsDeletedFalse(UUID userId, UUID bookId);

    Optional<Review> findByIdAndIsDeletedFalse(UUID id);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId AND r.status = 'APPROVED' AND r.isDeleted = false")
    Double findAverageRatingByBookId(@Param("bookId") UUID bookId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.book.id = :bookId AND r.status = 'APPROVED' AND r.isDeleted = false")
    Long countApprovedByBookId(@Param("bookId") UUID bookId);
}
