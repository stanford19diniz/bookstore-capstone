package com.bookstore.repository;

import com.bookstore.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {
    Optional<Shipment> findByOrderIdAndIsDeletedFalse(UUID orderId);
    Optional<Shipment> findByIdAndIsDeletedFalse(UUID id);
}
