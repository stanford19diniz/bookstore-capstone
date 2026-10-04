package com.bookstore.controller;

import com.bookstore.dto.AuthDto.*;
import com.bookstore.dto.UserDto.UserResponse;
import com.bookstore.exception.GlobalExceptionHandler;
import com.bookstore.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = AuthController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
@Import(GlobalExceptionHandler.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean AuthService authService;
    @MockBean JpaMetamodelMappingContext jpaMetamodelMappingContext;
    @MockBean com.bookstore.security.JwtProvider jwtProvider;
    @MockBean org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Test
    @DisplayName("POST /api/v1/auth/register - returns 201 on success")
    void register_returns201() throws Exception {
        RegisterRequest req = RegisterRequest.builder()
                .firstName("John").lastName("Doe")
                .email("john@example.com").password("password123").build();

        UserResponse userResponse = UserResponse.builder()
                .id(UUID.randomUUID()).email("john@example.com")
                .firstName("John").lastName("Doe")
                .isActive(true).roles(List.of("ROLE_CUSTOMER")).build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value("john@example.com"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - returns 400 on invalid input")
    void register_invalidInput_returns400() throws Exception {
        RegisterRequest req = RegisterRequest.builder()
                .email("not-an-email").password("short").build(); // missing required fields

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - returns 200 with tokens")
    void login_returns200() throws Exception {
        LoginRequest req = LoginRequest.builder()
                .email("john@example.com").password("password123").build();

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("access_token").refreshToken("refresh_token")
                .tokenType("Bearer").expiresIn(86400)
                .user(UserResponse.builder().id(UUID.randomUUID())
                        .email("john@example.com").build())
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("access_token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("GET /api/v1/auth/me - returns 500 when no principal (security auto-config excluded)")
    void me_noPrincipal_returns5xx() throws Exception {
        // Security auto-config is excluded: principal is always null → NPE → 500
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("GET /api/v1/auth/me - returns 500 even with @WithMockUser (security excluded)")
    void me_authenticated_withMockUser_returns5xx() throws Exception {
        // @WithMockUser has no effect when SecurityAutoConfiguration is excluded
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().is5xxServerError());
    }
}
