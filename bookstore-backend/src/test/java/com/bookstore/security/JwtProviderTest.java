package com.bookstore.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JwtProvider Unit Tests")
class JwtProviderTest {

    private JwtProvider jwtProvider;
    private static final String SECRET =
            "bookstore-test-secret-key-must-be-at-least-256-bits-long-for-hs256";

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(SECRET, 86400000L, 604800000L);
    }

    @Test
    @DisplayName("generateAccessToken - returns non-null token")
    void generateAccessToken_returnsToken() {
        String token = jwtProvider.generateAccessToken("test@example.com");
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("validateToken - valid token returns true")
    void validateToken_validToken_returnsTrue() {
        String token = jwtProvider.generateAccessToken("test@example.com");
        assertThat(jwtProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("validateToken - invalid token returns false")
    void validateToken_invalidToken_returnsFalse() {
        assertThat(jwtProvider.validateToken("not.a.valid.token")).isFalse();
    }

    @Test
    @DisplayName("getEmailFromToken - returns correct email")
    void getEmailFromToken_returnsEmail() {
        String token = jwtProvider.generateAccessToken("test@example.com");
        assertThat(jwtProvider.getEmailFromToken(token)).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("generateRefreshToken - different from access token")
    void generateRefreshToken_isDifferent() {
        String access = jwtProvider.generateAccessToken("test@example.com");
        String refresh = jwtProvider.generateRefreshToken("test@example.com");
        assertThat(access).isNotEqualTo(refresh);
    }

    @Test
    @DisplayName("validateToken - expired token returns false")
    void validateToken_expiredToken_returnsFalse() {
        // create provider with 1ms expiry
        JwtProvider shortLived = new JwtProvider(SECRET, 1L, 1L);
        String token = shortLived.generateAccessToken("test@example.com");
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}
        assertThat(shortLived.validateToken(token)).isFalse();
    }
}
