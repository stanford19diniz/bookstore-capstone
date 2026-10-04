package com.bookstore.service;

import com.bookstore.dto.OrderDto.*;
import com.bookstore.entity.*;
import com.bookstore.exception.BusinessRuleException;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock CartRepository cartRepository;
    @Mock AddressRepository addressRepository;
    @Mock CouponRepository couponRepository;
    @Mock PaymentRepository paymentRepository;
    @Mock ShipmentRepository shipmentRepository;
    @Mock UserRepository userRepository;
    @Mock BookService bookService;

    @InjectMocks OrderService orderService;

    private User user;
    private Address address;
    private Book book;
    private Cart cart;
    private CartItem cartItem;
    private Order order;

    @BeforeEach
    void setUp() {
        user = User.builder().email("test@example.com")
                .firstName("John").lastName("Doe").passwordHash("hash").build();
        user.setId(UUID.randomUUID());
        user.setIsDeleted(false);

        address = Address.builder().user(user).line1("123 Main St")
                .city("Mumbai").state("MH").country("India").pincode("400001").build();
        address.setId(UUID.randomUUID());
        address.setIsDeleted(false);

        book = Book.builder().title("1984").isbn("9780451524935")
                .price(new BigDecimal("299.00")).stockQuantity(10).deliveryDays(3).build();
        book.setId(UUID.randomUUID());

        cartItem = CartItem.builder().book(book).quantity(2)
                .unitPrice(new BigDecimal("299.00")).build();
        cartItem.setId(UUID.randomUUID());
        cartItem.setIsDeleted(false);

        cart = Cart.builder().user(user).status("ACTIVE")
                .items(new ArrayList<>(List.of(cartItem))).build();
        cart.setId(UUID.randomUUID());
        cart.setIsDeleted(false);
        cartItem.setCart(cart);

        order = Order.builder().user(user).address(address)
                .status("PENDING")
                .subtotal(new BigDecimal("598.00"))
                .discountAmount(BigDecimal.ZERO)
                .shippingCost(new BigDecimal("49.99"))
                .totalAmount(new BigDecimal("647.99"))
                .placedAt(Instant.now())
                .cancelDeadline(Instant.now().plus(48, ChronoUnit.HOURS))
                .items(new ArrayList<>())
                .build();
        order.setId(UUID.randomUUID());
        order.setIsDeleted(false);
    }

    @Test
    @DisplayName("placeOrder - success creates order from cart")
    void placeOrder_success() {
        PlaceOrderRequest req = PlaceOrderRequest.builder()
                .addressId(address.getId()).paymentMethod("CREDIT_CARD").build();

        when(userRepository.findByEmailAndIsDeletedFalse("test@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findActiveCartByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(addressRepository.findByIdAndUserIdAndIsDeletedFalse(address.getId(), user.getId()))
                .thenReturn(Optional.of(address));
        when(paymentRepository.findByOrderIdAndIsDeletedFalse(any())).thenReturn(Optional.empty());
        when(shipmentRepository.findByOrderIdAndIsDeletedFalse(any())).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        OrderResponse response = orderService.placeOrder("test@example.com", req);

        assertThat(response).isNotNull();
        verify(orderRepository).save(any(Order.class));
        assertThat(cart.getStatus()).isEqualTo("CHECKED_OUT");
    }

    @Test
    @DisplayName("placeOrder - throws BusinessRuleException when cart is empty")
    void placeOrder_emptyCart_throws() {
        cart.getItems().clear();
        PlaceOrderRequest req = PlaceOrderRequest.builder()
                .addressId(address.getId()).paymentMethod("CREDIT_CARD").build();

        when(userRepository.findByEmailAndIsDeletedFalse("test@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findActiveCartByUserId(user.getId())).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> orderService.placeOrder("test@example.com", req))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cart is empty");
    }

    @Test
    @DisplayName("cancelOrder - cancels within deadline")
    void cancelOrder_withinDeadline_success() {
        when(userRepository.findByEmailAndIsDeletedFalse("test@example.com")).thenReturn(Optional.of(user));
        when(orderRepository.findActiveById(order.getId())).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderIdAndIsDeletedFalse(any())).thenReturn(Optional.empty());
        when(shipmentRepository.findByOrderIdAndIsDeletedFalse(any())).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse response = orderService.cancelOrder(order.getId(), "test@example.com");

        assertThat(order.getStatus()).isEqualTo("CANCELLED");
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("cancelOrder - throws when deadline passed")
    void cancelOrder_pastDeadline_throws() {
        order.setCancelDeadline(Instant.now().minus(1, ChronoUnit.HOURS)); // expired

        when(userRepository.findByEmailAndIsDeletedFalse("test@example.com")).thenReturn(Optional.of(user));
        when(orderRepository.findActiveById(order.getId())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(order.getId(), "test@example.com"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cancellation deadline has passed");
    }

    @Test
    @DisplayName("listMyOrders - returns paginated orders for user")
    void listMyOrders_returnsPaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(order));

        when(userRepository.findByEmailAndIsDeletedFalse("test@example.com")).thenReturn(Optional.of(user));
        when(orderRepository.findByUserIdAndStatus(user.getId(), null, pageable)).thenReturn(page);

        Page<OrderSummaryResponse> result = orderService.listMyOrders("test@example.com", null, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}
