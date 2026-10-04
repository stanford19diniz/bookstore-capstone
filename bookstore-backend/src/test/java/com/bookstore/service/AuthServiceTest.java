package com.bookstore.service;

import com.bookstore.dto.AuthDto.*;
import com.bookstore.dto.UserDto.UserResponse;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.exception.ConflictException;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtProvider jwtProvider;
    @Mock AuthenticationManager authenticationManager;

    @InjectMocks AuthService authService;

    private Role customerRole;
    private User existingUser;

    @BeforeEach
    void setUp() {
        customerRole = Role.builder().name("ROLE_CUSTOMER").build();
        customerRole.setId(UUID.randomUUID());

        existingUser = User.builder()
                .email("test@example.com")
                .passwordHash("hashed")
                .firstName("John")
                .lastName("Doe")
                .build();
        existingUser.setId(UUID.randomUUID());
        existingUser.setRoles(Set.of(customerRole));
    }

    @Test
    @DisplayName("register - success creates new user")
    void register_success() {
        RegisterRequest req = RegisterRequest.builder()
                .firstName("John").lastName("Doe")
                .email("new@example.com").password("password123").build();

        when(userRepository.existsByEmailAndIsDeletedFalse("new@example.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_CUSTOMER")).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("password123")).thenReturn("hashed_pw");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });

        UserResponse result = authService.register(req);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getFirstName()).isEqualTo("John");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register - throws ConflictException when email already exists")
    void register_duplicateEmail_throwsConflict() {
        RegisterRequest req = RegisterRequest.builder()
                .firstName("John").lastName("Doe")
                .email("test@example.com").password("password123").build();

        when(userRepository.existsByEmailAndIsDeletedFalse("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    @DisplayName("login - success returns tokens")
    void login_success() {
        LoginRequest req = LoginRequest.builder()
                .email("test@example.com").password("password123").build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmailAndIsDeletedFalse("test@example.com"))
                .thenReturn(Optional.of(existingUser));
        when(jwtProvider.generateAccessToken("test@example.com")).thenReturn("access_token");
        when(jwtProvider.generateRefreshToken("test@example.com")).thenReturn("refresh_token");

        AuthResponse result = authService.login(req);

        assertThat(result.getAccessToken()).isEqualTo("access_token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh_token");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getUser().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("getCurrentUser - returns user response")
    void getCurrentUser_success() {
        when(userRepository.findByEmailAndIsDeletedFalse("test@example.com"))
                .thenReturn(Optional.of(existingUser));

        UserResponse result = authService.getCurrentUser("test@example.com");

        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getFirstName()).isEqualTo("John");
    }
}
