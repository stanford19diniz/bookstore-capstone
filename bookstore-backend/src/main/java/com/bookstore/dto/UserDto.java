package com.bookstore.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

public class UserDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UserResponse {
        private UUID id;
        private String email;
        private String firstName;
        private String lastName;
        private String phone;
        private Boolean isActive;
        private List<String> roles;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UpdateUserRequest {
        @Size(min=1, max=100) private String firstName;
        @Size(min=1, max=100) private String lastName;
        @Size(max=20)         private String phone;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AddressRequest {
        @Size(max=50)                          private String label;
        @NotBlank @Size(min=1, max=255)        private String line1;
        @Size(max=255)                         private String line2;
        @NotBlank @Size(min=1, max=100)        private String city;
        @NotBlank @Size(min=1, max=100)        private String state;
        @NotBlank @Size(min=1, max=100)        private String country;
        @NotBlank @Size(min=1, max=20)         private String pincode;
        private Boolean isDefault = false;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AddressResponse {
        private UUID id;
        private String label;
        private String line1;
        private String line2;
        private String city;
        private String state;
        private String country;
        private String pincode;
        private Boolean isDefault;
    }
}
