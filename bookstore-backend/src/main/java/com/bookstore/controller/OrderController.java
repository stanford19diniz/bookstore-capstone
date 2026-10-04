package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.dto.OrderDto.*;
import com.bookstore.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @AuthenticationPrincipal UserDetails p, @Valid @RequestBody PlaceOrderRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderService.placeOrder(p.getUsername(), req)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @AuthenticationPrincipal UserDetails p, @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getById(id, p.getUsername())));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderSummaryResponse>>> myOrders(
            @AuthenticationPrincipal UserDetails p,
            @RequestParam(required=false) String status,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("placedAt").descending());
        return ResponseEntity.ok(ApiResponse.success(
                orderService.listMyOrders(p.getUsername(), status, pageable)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @AuthenticationPrincipal UserDetails p, @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.cancelOrder(id, p.getUsername())));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<ApiResponse<OrderResponse>> returnOrder(
            @AuthenticationPrincipal UserDetails p,
            @PathVariable UUID id,
            @Valid @RequestBody ReturnOrderRequest req) {
        return ResponseEntity.ok(ApiResponse.success(orderService.returnOrder(id, p.getUsername(), req)));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<OrderSummaryResponse>>> allOrders(
            @RequestParam(required=false) String status,
            @RequestParam(required=false) UUID userId,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("placedAt").descending());
        return ResponseEntity.ok(ApiResponse.success(
                orderService.listAllOrders(status, userId, pageable)));
    }
}
