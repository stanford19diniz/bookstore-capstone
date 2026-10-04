package com.bookstore.service;

import com.bookstore.dto.OrderDto.*;
import com.bookstore.dto.PaymentDto.PaymentSummary;
import com.bookstore.dto.ShipmentDto.ShipmentSummary;
import com.bookstore.dto.UserDto.AddressResponse;
import com.bookstore.entity.*;
import com.bookstore.exception.*;
import com.bookstore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final CouponRepository couponRepository;
    private final PaymentRepository paymentRepository;
    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;
    private final BookService bookService;

    private User loadUser(String email) {
        return userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    @Transactional
    public OrderResponse placeOrder(String email, PlaceOrderRequest req) {
        User user = loadUser(email);
        Cart cart = cartRepository.findActiveCartByUserId(user.getId())
                .orElseThrow(() -> new BusinessRuleException("ORD_001", "Cart is empty"));
        if (cart.getItems().isEmpty()) {
            throw new BusinessRuleException("ORD_001", "Cart is empty");
        }
        Address address = addressRepository.findByIdAndUserIdAndIsDeletedFalse(req.getAddressId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", req.getAddressId().toString()));

        Coupon coupon = null;
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (req.getCouponCode() != null && !req.getCouponCode().isBlank()) {
            coupon = couponRepository.findByCodeAndIsDeletedFalse(req.getCouponCode())
                    .orElseThrow(() -> new BusinessRuleException("ORD_005", "Invalid coupon code"));
            if (!coupon.getIsActive()) throw new BusinessRuleException("ORD_005", "Coupon is inactive");
            if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(Instant.now()))
                throw new BusinessRuleException("ORD_005", "Coupon has expired");
        }

        BigDecimal subtotal = cart.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (coupon != null) {
            if ("PERCENTAGE".equals(coupon.getDiscountType())) {
                discountAmount = subtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100));
            } else {
                discountAmount = coupon.getDiscountValue();
            }
            coupon.setUsedCount(coupon.getUsedCount() + 1);
        }

        BigDecimal shippingCost = new BigDecimal("49.99");
        BigDecimal totalAmount = subtotal.subtract(discountAmount).add(shippingCost);

        Order order = Order.builder()
                .user(user).address(address).coupon(coupon)
                .subtotal(subtotal).discountAmount(discountAmount)
                .shippingCost(shippingCost).totalAmount(totalAmount)
                .giftPointsUsed(req.getGiftPointsToRedeem() != null ? req.getGiftPointsToRedeem() : 0)
                .placedAt(Instant.now())
                .cancelDeadline(Instant.now().plus(48, ChronoUnit.HOURS))
                .build();

        cart.getItems().forEach(ci -> {
            OrderItem item = OrderItem.builder()
                    .order(order).book(ci.getBook()).quantity(ci.getQuantity())
                    .unitPrice(ci.getUnitPrice())
                    .estimatedDelivery(java.time.LocalDate.now().plusDays(ci.getBook().getDeliveryDays()))
                    .build();
            order.getItems().add(item);
        });

        cart.setStatus("CHECKED_OUT");
        cartRepository.save(cart);
        orderRepository.save(order);
        return toOrderResponse(order);
    }

    public OrderResponse getById(UUID orderId, String email) {
        User user = loadUser(email);
        Order order = orderRepository.findActiveById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId.toString()));
        if (!order.getUser().getId().equals(user.getId()))
            throw new BusinessRuleException("ORD_003", "Access denied to this order");
        return toOrderResponse(order);
    }

    public Page<OrderSummaryResponse> listMyOrders(String email, String status, Pageable pageable) {
        User user = loadUser(email);
        return orderRepository.findByUserIdAndStatus(user.getId(), status, pageable)
                .map(this::toSummary);
    }

    public Page<OrderSummaryResponse> listAllOrders(String status, UUID userId, Pageable pageable) {
        return orderRepository.findAllWithFilters(status, userId, pageable).map(this::toSummary);
    }

    @Transactional
    public OrderResponse cancelOrder(UUID orderId, String email) {
        User user = loadUser(email);
        Order order = orderRepository.findActiveById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId.toString()));
        if (!order.getUser().getId().equals(user.getId()))
            throw new BusinessRuleException("ORD_003", "Access denied");
        if (order.getCancelDeadline() != null && Instant.now().isAfter(order.getCancelDeadline()))
            throw new BusinessRuleException("ORD_004", "Cancellation deadline has passed");
        order.setStatus("CANCELLED");
        return toOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse returnOrder(UUID orderId, String email, ReturnOrderRequest req) {
        User user = loadUser(email);
        Order order = orderRepository.findActiveById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId.toString()));
        if (!order.getUser().getId().equals(user.getId()))
            throw new BusinessRuleException("ORD_003", "Access denied");
        if (!"DELIVERED".equals(order.getStatus()))
            throw new BusinessRuleException("ORD_007", "Only delivered orders can be returned");
        order.setStatus("RETURNED");
        order.setNotes("Return reason: " + req.getReason());
        return toOrderResponse(orderRepository.save(order));
    }

    private OrderSummaryResponse toSummary(Order o) {
        return OrderSummaryResponse.builder()
                .id(o.getId()).status(o.getStatus())
                .totalAmount(o.getTotalAmount())
                .itemCount(o.getItems().size())
                .placedAt(o.getPlacedAt())
                .cancelDeadline(o.getCancelDeadline())
                .build();
    }

    private OrderResponse toOrderResponse(Order o) {
        List<OrderItemResponse> items = o.getItems().stream().map(i ->
                OrderItemResponse.builder()
                        .bookId(i.getBook().getId())
                        .title(i.getBook().getTitle())
                        .coverImageUrl(i.getBook().getCoverImageUrl())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .lineTotal(i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                        .estimatedDelivery(i.getEstimatedDelivery())
                        .build()).collect(Collectors.toList());

        Address addr = o.getAddress();
        AddressResponse addressResp = AddressResponse.builder()
                .id(addr.getId()).label(addr.getLabel()).line1(addr.getLine1())
                .line2(addr.getLine2()).city(addr.getCity()).state(addr.getState())
                .country(addr.getCountry()).pincode(addr.getPincode())
                .isDefault(addr.getIsDefault()).build();

        Payment payment = paymentRepository.findByOrderIdAndIsDeletedFalse(o.getId()).orElse(null);
        PaymentSummary paymentSummary = payment != null ? PaymentSummary.builder()
                .method(payment.getMethod()).status(payment.getStatus())
                .amount(payment.getAmount()).paidAt(payment.getPaidAt()).build() : null;

        Shipment shipment = shipmentRepository.findByOrderIdAndIsDeletedFalse(o.getId()).orElse(null);
        ShipmentSummary shipmentSummary = shipment != null ? ShipmentSummary.builder()
                .status(shipment.getStatus()).trackingNumber(shipment.getTrackingNumber())
                .carrier(shipment.getCarrier()).estimatedDelivery(shipment.getEstimatedDelivery()).build() : null;

        return OrderResponse.builder()
                .id(o.getId()).status(o.getStatus()).items(items).address(addressResp)
                .subtotal(o.getSubtotal()).discountAmount(o.getDiscountAmount())
                .shippingCost(o.getShippingCost()).totalAmount(o.getTotalAmount())
                .giftPointsUsed(o.getGiftPointsUsed()).placedAt(o.getPlacedAt())
                .cancelDeadline(o.getCancelDeadline())
                .payment(paymentSummary).shipment(shipmentSummary).build();
    }
}
