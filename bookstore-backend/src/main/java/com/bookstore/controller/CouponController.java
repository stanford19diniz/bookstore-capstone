package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.dto.CouponDto.*;
import com.bookstore.entity.Coupon;
import com.bookstore.exception.*;
import com.bookstore.repository.CouponRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponRepository couponRepository;

    private CouponResponse toResponse(Coupon c) {
        return CouponResponse.builder()
                .id(c.getId()).code(c.getCode()).description(c.getDescription())
                .discountType(c.getDiscountType()).discountValue(c.getDiscountValue())
                .minOrderValue(c.getMinOrderValue()).maxUses(c.getMaxUses())
                .usedCount(c.getUsedCount()).expiresAt(c.getExpiresAt())
                .isActive(c.getIsActive()).build();
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<CouponResponse>> validateCoupon(
            @RequestParam String code,
            @RequestParam BigDecimal orderTotal) {
        Coupon c = couponRepository.findByCodeAndIsDeletedFalse(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", code));
        if (!c.getIsActive())
            throw new BusinessRuleException("CPN_002", "Coupon is inactive");
        if (c.getExpiresAt() != null && c.getExpiresAt().isBefore(Instant.now()))
            throw new BusinessRuleException("CPN_002", "Coupon has expired");
        if (c.getMaxUses() != null && c.getUsedCount() >= c.getMaxUses())
            throw new BusinessRuleException("CPN_003", "Coupon usage limit reached");
        if (orderTotal.compareTo(c.getMinOrderValue()) < 0)
            throw new BusinessRuleException("CPN_004",
                    "Order total must be at least " + c.getMinOrderValue());
        return ResponseEntity.ok(ApiResponse.success(toResponse(c)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CouponResponse>>> listCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                couponRepository.findAllByIsDeletedFalse(PageRequest.of(page, size))
                        .map(this::toResponse)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(@Valid @RequestBody CouponRequest req) {
        if (couponRepository.existsByCodeAndIsDeletedFalse(req.getCode()))
            throw new ConflictException("CPN_005", "Coupon code already exists: " + req.getCode());
        Coupon c = Coupon.builder()
                .code(req.getCode().toUpperCase()).description(req.getDescription())
                .discountType(req.getDiscountType()).discountValue(req.getDiscountValue())
                .minOrderValue(req.getMinOrderValue()).maxUses(req.getMaxUses())
                .expiresAt(req.getExpiresAt()).isActive(true).build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(toResponse(couponRepository.save(c))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> updateCoupon(
            @PathVariable UUID id, @Valid @RequestBody CouponRequest req) {
        Coupon c = couponRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", id.toString()));
        c.setDiscountType(req.getDiscountType()); c.setDiscountValue(req.getDiscountValue());
        c.setMinOrderValue(req.getMinOrderValue()); c.setMaxUses(req.getMaxUses());
        c.setExpiresAt(req.getExpiresAt());
        return ResponseEntity.ok(ApiResponse.success(toResponse(couponRepository.save(c))));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<CouponResponse>> deactivateCoupon(@PathVariable UUID id) {
        Coupon c = couponRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", id.toString()));
        c.setIsActive(false);
        return ResponseEntity.ok(ApiResponse.success(toResponse(couponRepository.save(c))));
    }
}
