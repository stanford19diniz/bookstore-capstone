package com.bookstore.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ReviewDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ReviewRequest {
        @NotNull private UUID bookId;
        @NotNull @Min(1) @Max(5) private Integer rating;
        @Size(max=2000) private String comment;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ReviewResponse {
        private UUID id;
        private UUID bookId;
        private UUID userId;
        private String userFirstName;
        private Integer rating;
        private String comment;
        private String status;
        private Instant createdAt;
    }
}
