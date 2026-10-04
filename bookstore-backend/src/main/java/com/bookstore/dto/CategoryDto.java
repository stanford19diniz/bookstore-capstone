package com.bookstore.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.UUID;

public class CategoryDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CategoryRequest {
        @NotBlank @Size(min=1,max=100) private String name;
        @NotBlank @Size(min=1,max=100) private String slug;
        private UUID parentId;
        private Integer displayOrder = 0;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CategoryResponse {
        private UUID id;
        private String name;
        private String slug;
        private UUID parentId;
        private Integer displayOrder;
    }
}
