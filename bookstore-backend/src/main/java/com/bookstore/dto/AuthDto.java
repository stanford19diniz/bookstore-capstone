package com.bookstore.dto;

import jakarta.validation.constraints.*;
import lombok.*;

public class AuthDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RegisterRequest {
        @NotBlank @Size(min=1, max=100) private String firstName;
        @NotBlank @Size(min=1, max=100) private String lastName;
        @NotBlank @Email private String email;
        @NotBlank @Size(min=8, max=100) private String password;
        @Size(max=20) private String phone;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class LoginRequest {
        @NotBlank @Email private String email;
        @NotBlank private String password;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RefreshTokenRequest {
        @NotBlank private String refreshToken;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private long expiresIn;
        private UserDto.UserResponse user;
    }
}
