package com.bookstore.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class CouponDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CouponRequest {
        @NotBlank @Size(min=3,max=50)       private String code;
        private String description;
        @NotBlank                           private String discountType;
        @NotNull @DecimalMin("0.01")        private BigDecimal discountValue;
        @NotNull @DecimalMin("0.00")        private BigDecimal minOrderValue;
        @Min(1) private Integer maxUses;
        private Instant expiresAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CouponResponse {
        private UUID id;
        private String code;
        private String description;
        private String discountType;
        private BigDecimal discountValue;
        private BigDecimal minOrderValue;
        private Integer maxUses;
        private Integer usedCount;
        private Instant expiresAt;
        private Boolean isActive;
    }
}
