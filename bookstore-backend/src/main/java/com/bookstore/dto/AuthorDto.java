package com.bookstore.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.UUID;

public class AuthorDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AuthorRequest {
        @NotBlank @Size(min=1,max=100) private String firstName;
        @NotBlank @Size(min=1,max=100) private String lastName;
        private String bio;
        private String profileImageUrl;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AuthorResponse {
        private UUID id;
        private String firstName;
        private String lastName;
        private String bio;
        private String profileImageUrl;
    }
}
