package com.bookstore.repository;

import com.bookstore.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    Optional<Coupon> findByCodeAndIsDeletedFalse(String code);
    Page<Coupon> findAllByIsDeletedFalse(Pageable pageable);
    Optional<Coupon> findByIdAndIsDeletedFalse(UUID id);
    boolean existsByCodeAndIsDeletedFalse(String code);
}
