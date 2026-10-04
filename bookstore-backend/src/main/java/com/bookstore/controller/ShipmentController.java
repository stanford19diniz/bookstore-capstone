package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.dto.ShipmentDto.*;
import com.bookstore.entity.Shipment;
import com.bookstore.exception.*;
import com.bookstore.repository.ShipmentRepository;
import com.bookstore.repository.ShippingRateRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentRepository shipmentRepository;
    private final ShippingRateRepository shippingRateRepository;

    private ShipmentResponse toResponse(Shipment s) {
        return ShipmentResponse.builder()
                .id(s.getId()).orderId(s.getOrder().getId())
                .carrier(s.getCarrier()).trackingNumber(s.getTrackingNumber())
                .status(s.getStatus()).estimatedDelivery(s.getEstimatedDelivery())
                .dispatchedAt(s.getDispatchedAt()).deliveredAt(s.getDeliveredAt()).build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentByOrderId(@PathVariable UUID orderId) {
        Shipment s = shipmentRepository.findByOrderIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", orderId.toString()));
        return ResponseEntity.ok(ApiResponse.success(toResponse(s)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateShipment(
            @PathVariable UUID id, @Valid @RequestBody ShipmentUpdateRequest req) {
        Shipment s = shipmentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", id.toString()));
        s.setStatus(req.getStatus());
        if (req.getCarrier() != null) s.setCarrier(req.getCarrier());
        if (req.getTrackingNumber() != null) s.setTrackingNumber(req.getTrackingNumber());
        if (req.getEstimatedDelivery() != null) s.setEstimatedDelivery(req.getEstimatedDelivery());
        if ("DISPATCHED".equals(req.getStatus()) && s.getDispatchedAt() == null)
            s.setDispatchedAt(java.time.Instant.now());
        if ("DELIVERED".equals(req.getStatus()) && s.getDeliveredAt() == null)
            s.setDeliveredAt(java.time.Instant.now());
        return ResponseEntity.ok(ApiResponse.success(toResponse(shipmentRepository.save(s))));
    }

    @PostMapping("/calculate-rate")
    public ResponseEntity<ApiResponse<ShippingRateResponse>> calculateRate(
            @Valid @RequestBody ShippingRateRequest req) {
        // Simple rule: international if pincode starts with non-digit, else domestic
        String zone = req.getPincode().matches("\\d+") ? "DOMESTIC" : "INTERNATIONAL";
        double weight = req.getWeightKg();
        double rate = shippingRateRepository.findRateByZoneAndWeight(zone, weight)
                .map(java.math.BigDecimal::doubleValue).orElse(99.99);
        int deliveryDays = "DOMESTIC".equals(zone) ? 5 : 14;
        return ResponseEntity.ok(ApiResponse.success(
                ShippingRateResponse.builder().zone(zone).rate(rate)
                        .estimatedDeliveryDays(deliveryDays).build()));
    }
}
