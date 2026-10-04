package com.bookstore.service;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.CartDto.*;
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

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService Unit Tests")
class CartServiceTest {

    @Mock CartRepository cartRepository;
    @Mock BookRepository bookRepository;
    @Mock UserRepository userRepository;
    @Mock BookService bookService;

    @InjectMocks CartService cartService;

    private User user;
    private Book book;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = User.builder().email("test@example.com")
                .firstName("John").lastName("Doe").passwordHash("hash").build();
        user.setId(UUID.randomUUID());
        user.setIsDeleted(false);

        book = Book.builder().title("1984").isbn("9780451524935")
                .price(new BigDecimal("299.00")).stockQuantity(10)
                .deliveryDays(3).language("English").build();
        book.setId(UUID.randomUUID());
        book.setIsDeleted(false);

        cart = Cart.builder().user(user).status("ACTIVE").build();
        cart.setId(UUID.randomUUID());
        cart.setIsDeleted(false);
    }

    @Test
    @DisplayName("addItem - creates new cart and adds book")
    void addItem_createsCartAndAddsBook() {
        CartItemRequest req = new CartItemRequest(book.getId(), 2);

        when(userRepository.findByEmailAndIsDeletedFalse("test@example.com"))
                .thenReturn(Optional.of(user));
        when(bookRepository.findByIdAndIsDeletedFalse(book.getId()))
                .thenReturn(Optional.of(book));
        when(cartRepository.findActiveCartByUserId(user.getId()))
                .thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(bookService.toSummary(any(Book.class))).thenReturn(
                BookDto.BookSummaryResponse.builder().id(book.getId()).title("1984")
                        .price(book.getPrice()).build());

        CartResponse response = cartService.addItem("test@example.com", req);

        assertThat(response).isNotNull();
        verify(cartRepository, atLeastOnce()).save(any(Cart.class));
    }

    @Test
    @DisplayName("addItem - throws BusinessRuleException when insufficient stock")
    void addItem_insufficientStock_throws() {
        book.setStockQuantity(1);
        CartItemRequest req = new CartItemRequest(book.getId(), 5);

        when(userRepository.findByEmailAndIsDeletedFalse("test@example.com"))
                .thenReturn(Optional.of(user));
        when(bookRepository.findByIdAndIsDeletedFalse(book.getId()))
                .thenReturn(Optional.of(book));

        assertThatThrownBy(() -> cartService.addItem("test@example.com", req))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    @DisplayName("addItem - throws ResourceNotFoundException when book not found")
    void addItem_bookNotFound_throws() {
        CartItemRequest req = new CartItemRequest(UUID.randomUUID(), 1);

        when(userRepository.findByEmailAndIsDeletedFalse("test@example.com"))
                .thenReturn(Optional.of(user));
        when(bookRepository.findByIdAndIsDeletedFalse(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addItem("test@example.com", req))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("getCart - returns empty cart when none exists")
    void getCart_noExistingCart_returnsEmpty() {
        when(userRepository.findByEmailAndIsDeletedFalse("test@example.com"))
                .thenReturn(Optional.of(user));
        when(cartRepository.findActiveCartByUserId(user.getId()))
                .thenReturn(Optional.empty());

        CartResponse response = cartService.getCart("test@example.com");

        assertThat(response).isNotNull();
        assertThat(response.getItems()).isEmpty();
        assertThat(response.getSubtotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
