package com.bookstore.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PaymentDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PaymentRequest {
        @NotNull private UUID orderId;
        @NotBlank private String method;
        @NotNull @DecimalMin("0.01") private BigDecimal amount;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PaymentResponse {
        private UUID id;
        private UUID orderId;
        private String method;
        private String status;
        private BigDecimal amount;
        private String transactionRef;
        private Instant paidAt;
        private Instant refundedAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PaymentSummary {
        private String method;
        private String status;
        private BigDecimal amount;
        private Instant paidAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TopUpRequest {
        @NotNull @DecimalMin("1.00") private BigDecimal amount;
    }
}
