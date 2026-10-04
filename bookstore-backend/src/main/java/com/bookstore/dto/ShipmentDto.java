package com.bookstore.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class ShipmentDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ShipmentResponse {
        private UUID id;
        private UUID orderId;
        private String carrier;
        private String trackingNumber;
        private String status;
        private LocalDate estimatedDelivery;
        private Instant dispatchedAt;
        private Instant deliveredAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ShipmentSummary {
        private String status;
        private String trackingNumber;
        private String carrier;
        private LocalDate estimatedDelivery;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ShipmentUpdateRequest {
        @NotBlank private String status;
        private String carrier;
        private String trackingNumber;
        private LocalDate estimatedDelivery;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ShippingRateRequest {
        @NotBlank private String pincode;
        @NotNull @DecimalMin("0.001") private Double weightKg;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ShippingRateResponse {
        private String zone;
        private Double rate;
        private Integer estimatedDeliveryDays;
    }
}
