package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.dto.CartDto.*;
import com.bookstore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@AuthenticationPrincipal UserDetails p) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(p.getUsername())));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Object>> clearCart(@AuthenticationPrincipal UserDetails p) {
        cartService.clearCart(p.getUsername());
        return ResponseEntity.ok(ApiResponse.message("Cart cleared"));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @AuthenticationPrincipal UserDetails p, @RequestBody CartItemRequest req) {
        return ResponseEntity.ok(ApiResponse.success(cartService.addItem(p.getUsername(), req)));
    }

    @PatchMapping("/items/{bookId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @AuthenticationPrincipal UserDetails p,
            @PathVariable UUID bookId,
            @RequestBody UpdateQtyRequest req) {
        return ResponseEntity.ok(ApiResponse.success(
                cartService.updateItem(p.getUsername(), bookId, req.getQuantity())));
    }

    @DeleteMapping("/items/{bookId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @AuthenticationPrincipal UserDetails p, @PathVariable UUID bookId) {
        return ResponseEntity.ok(ApiResponse.success(cartService.removeItem(p.getUsername(), bookId)));
    }
}
