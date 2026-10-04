package com.bookstore.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class OrderDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PlaceOrderRequest {
        @NotNull private UUID addressId;
        private String couponCode;
        @Min(0) private Integer giftPointsToRedeem = 0;
        @NotBlank private String paymentMethod;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ReturnOrderRequest {
        @NotBlank @Size(min=10, max=500) private String reason;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OrderItemResponse {
        private UUID bookId;
        private String title;
        private String coverImageUrl;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;
        private LocalDate estimatedDelivery;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OrderSummaryResponse {
        private UUID id;
        private String status;
        private BigDecimal totalAmount;
        private Integer itemCount;
        private Instant placedAt;
        private Instant cancelDeadline;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OrderResponse {
        private UUID id;
        private String status;
        private List<OrderItemResponse> items;
        private UserDto.AddressResponse address;
        private BigDecimal subtotal;
        private BigDecimal discountAmount;
        private BigDecimal shippingCost;
        private BigDecimal totalAmount;
        private Integer giftPointsUsed;
        private Instant placedAt;
        private Instant cancelDeadline;
        private PaymentDto.PaymentSummary payment;
        private ShipmentDto.ShipmentSummary shipment;
    }
}
