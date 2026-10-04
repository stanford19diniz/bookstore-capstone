package com.bookstore.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class CartDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CartItemRequest {
        private UUID bookId;
        private Integer quantity;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UpdateQtyRequest {
        private Integer quantity;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CartItemResponse {
        private UUID bookId;
        private BookDto.BookSummaryResponse book;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CartResponse {
        private UUID id;
        private String status;
        private List<CartItemResponse> items;
        private BigDecimal subtotal;
        private Integer itemCount;
        private List<BookDto.BookSummaryResponse> recommendations;
    }
}
