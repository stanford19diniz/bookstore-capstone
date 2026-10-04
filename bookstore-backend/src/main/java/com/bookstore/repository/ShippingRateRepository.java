package com.bookstore.repository;

import com.bookstore.entity.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ShippingRateRepository extends JpaRepository<ShippingRate, UUID> {

    @Query("""
        SELECT s.rate FROM ShippingRate s
        WHERE s.zone = :zone
          AND s.weightFrom <= :weight
          AND s.weightTo >= :weight
          AND s.isDeleted = false
        ORDER BY s.weightFrom ASC
        """)
    Optional<java.math.BigDecimal> findRateByZoneAndWeight(@Param("zone") String zone, @Param("weight") double weight);
}
