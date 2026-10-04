package com.bookstore.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BookDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class BookRequest {
        @NotBlank @Size(min=1,max=500)            private String title;
        @NotBlank @Pattern(regexp="^97[89]\\d{10}$") private String isbn;
        @NotNull                                  private UUID publisherId;
        private String description;
        @NotNull @DecimalMin("0.00")              private BigDecimal price;
        @Min(0)                                   private Integer stockQuantity;
        private String coverImageUrl;
        private String language = "English";
        private Integer pages;
        private LocalDate publishedDate;
        @Min(1)                                   private Integer deliveryDays = 5;
        @NotEmpty                                 private List<UUID> authorIds;
        @NotEmpty                                 private List<UUID> categoryIds;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class BookSummaryResponse {
        private UUID id;
        private String title;
        private String isbn;
        private BigDecimal price;
        private String coverImageUrl;
        private BigDecimal averageRating;
        private Integer reviewCount;
        private Integer deliveryDays;
        private List<AuthorDto.AuthorResponse> authors;
        private List<CategoryDto.CategoryResponse> categories;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class BookDetailResponse {
        private UUID id;
        private String title;
        private String isbn;
        private BigDecimal price;
        private String coverImageUrl;
        private BigDecimal averageRating;
        private Integer reviewCount;
        private Integer deliveryDays;
        private List<AuthorDto.AuthorResponse> authors;
        private List<CategoryDto.CategoryResponse> categories;
        private String description;
        private PublisherDto.PublisherResponse publisher;
        private String language;
        private Integer pages;
        private LocalDate publishedDate;
        private Integer stockQuantity;
        private Boolean isActive;
    }
}
