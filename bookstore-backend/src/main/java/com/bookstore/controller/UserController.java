package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.dto.UserDto.*;
import com.bookstore.entity.Address;
import com.bookstore.entity.User;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.AddressRepository;
import com.bookstore.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    // ── helpers ──────────────────────────────────────────────────────────────

    private User loadUser(UUID id) {
        return userRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
    }

    private UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .phone(u.getPhone())
                .isActive(u.getIsActive())
                .roles(u.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList()))
                .build();
    }

    private AddressResponse toAddressResponse(Address a) {
        return AddressResponse.builder()
                .id(a.getId())
                .label(a.getLabel())
                .line1(a.getLine1())
                .line2(a.getLine2())
                .city(a.getCity())
                .state(a.getState())
                .country(a.getCountry())
                .pincode(a.getPincode())
                .isDefault(a.getIsDefault())
                .build();
    }

    // ── endpoints ─────────────────────────────────────────────────────────────

    /** GET /api/v1/users — Admin only (secured in SecurityConfig) */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<UserResponse> result = userRepository
                .findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(this::toResponse);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /** GET /api/v1/users/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.success(toResponse(loadUser(id))));
    }

    /** PUT /api/v1/users/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        User user = loadUser(id);
        if (req.getFirstName() != null) user.setFirstName(req.getFirstName());
        if (req.getLastName()  != null) user.setLastName(req.getLastName());
        if (req.getPhone()     != null) user.setPhone(req.getPhone());
        return ResponseEntity.ok(ApiResponse.success(toResponse(userRepository.save(user))));
    }

    /** DELETE /api/v1/users/{id} — Admin only */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deactivateUser(@PathVariable UUID id) {
        User user = loadUser(id);
        user.setIsActive(false);
        user.softDelete();
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.message("User deactivated"));
    }

    // ── addresses ─────────────────────────────────────────────────────────────

    /** GET /api/v1/users/{id}/addresses */
    @GetMapping("/{id}/addresses")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> listAddresses(@PathVariable UUID id) {
        List<AddressResponse> list = addressRepository.findByUserIdAndIsDeletedFalse(id)
                .stream().map(this::toAddressResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    /** POST /api/v1/users/{id}/addresses */
    @PostMapping("/{id}/addresses")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @PathVariable UUID id,
            @Valid @RequestBody AddressRequest req) {
        User user = loadUser(id);

        // if new address is default, clear existing default
        if (Boolean.TRUE.equals(req.getIsDefault())) {
            addressRepository.findByUserIdAndIsDeletedFalse(id).stream()
                    .filter(Address::getIsDefault)
                    .forEach(a -> { a.setIsDefault(false); addressRepository.save(a); });
        }

        Address address = Address.builder()
                .user(user)
                .label(req.getLabel())
                .line1(req.getLine1())
                .line2(req.getLine2())
                .city(req.getCity())
                .state(req.getState())
                .country(req.getCountry())
                .pincode(req.getPincode())
                .isDefault(req.getIsDefault() != null && req.getIsDefault())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(toAddressResponse(addressRepository.save(address))));
    }

    /** PUT /api/v1/users/{id}/addresses/{addressId} */
    @PutMapping("/{id}/addresses/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable UUID id,
            @PathVariable UUID addressId,
            @Valid @RequestBody AddressRequest req) {
        Address address = addressRepository.findByIdAndUserIdAndIsDeletedFalse(addressId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", addressId.toString()));

        if (Boolean.TRUE.equals(req.getIsDefault())) {
            addressRepository.findByUserIdAndIsDeletedFalse(id).stream()
                    .filter(a -> !a.getId().equals(addressId) && a.getIsDefault())
                    .forEach(a -> { a.setIsDefault(false); addressRepository.save(a); });
        }

        address.setLabel(req.getLabel());
        address.setLine1(req.getLine1());
        address.setLine2(req.getLine2());
        address.setCity(req.getCity());
        address.setState(req.getState());
        address.setCountry(req.getCountry());
        address.setPincode(req.getPincode());
        if (req.getIsDefault() != null) address.setIsDefault(req.getIsDefault());
        return ResponseEntity.ok(ApiResponse.success(toAddressResponse(addressRepository.save(address))));
    }

    /** DELETE /api/v1/users/{id}/addresses/{addressId} */
    @DeleteMapping("/{id}/addresses/{addressId}")
    public ResponseEntity<ApiResponse<Object>> deleteAddress(
            @PathVariable UUID id,
            @PathVariable UUID addressId) {
        Address address = addressRepository.findByIdAndUserIdAndIsDeletedFalse(addressId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", addressId.toString()));
        address.softDelete();
        addressRepository.save(address);
        return ResponseEntity.ok(ApiResponse.message("Address deleted"));
    }
}
