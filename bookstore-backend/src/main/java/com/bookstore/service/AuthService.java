package com.bookstore.service;

import com.bookstore.dto.AuthDto.*;
import com.bookstore.dto.UserDto.*;
import com.bookstore.entity.*;
import com.bookstore.exception.*;
import com.bookstore.repository.*;
import com.bookstore.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public UserResponse register(RegisterRequest req) {
        if (userRepository.existsByEmailAndIsDeletedFalse(req.getEmail())) {
            throw new ConflictException("AUTH_002", "Email already registered: " + req.getEmail());
        }
        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new BookstoreException("SYS_001", "Role not found", org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR));

        User user = User.builder()
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .phone(req.getPhone())
                .build();
        user.getRoles().add(customerRole);
        user = userRepository.save(user);
        return toUserResponse(user);
    }

    public AuthResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        User user = userRepository.findByEmailAndIsDeletedFalse(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", req.getEmail()));

        return AuthResponse.builder()
                .accessToken(jwtProvider.generateAccessToken(req.getEmail()))
                .refreshToken(jwtProvider.generateRefreshToken(req.getEmail()))
                .tokenType("Bearer")
                .expiresIn(86400)
                .user(toUserResponse(user))
                .build();
    }

    public AuthResponse refresh(RefreshTokenRequest req) {
        if (!jwtProvider.validateToken(req.getRefreshToken())) {
            throw new BookstoreException("AUTH_004", "Invalid refresh token", org.springframework.http.HttpStatus.UNAUTHORIZED);
        }
        String email = jwtProvider.getEmailFromToken(req.getRefreshToken());
        return AuthResponse.builder()
                .accessToken(jwtProvider.generateAccessToken(email))
                .refreshToken(jwtProvider.generateRefreshToken(email))
                .tokenType("Bearer")
                .expiresIn(86400)
                .build();
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
        return toUserResponse(user);
    }

    private UserResponse toUserResponse(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName).collect(Collectors.toList());
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .isActive(user.getIsActive())
                .roles(roles)
                .build();
    }
}
