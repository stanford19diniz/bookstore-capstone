package com.bookstore.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.UUID;

public class PublisherDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PublisherRequest {
        @NotBlank @Size(min=1,max=255) private String name;
        @Size(max=100)                 private String country;
        private String websiteUrl;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PublisherResponse {
        private UUID id;
        private String name;
        private String country;
        private String websiteUrl;
    }
}
