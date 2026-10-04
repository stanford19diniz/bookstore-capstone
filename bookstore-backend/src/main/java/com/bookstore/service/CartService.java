package com.bookstore.service;

import com.bookstore.dto.CartDto.*;
import com.bookstore.entity.*;
import com.bookstore.exception.*;
import com.bookstore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookService bookService;

    // ── helpers ──────────────────────────────────────────────────────────────

    private User loadUser(String email) {
        return userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findActiveCartByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(
                        Cart.builder().user(user).status("ACTIVE").build()));
    }

    // ── public API ───────────────────────────────────────────────────────────

    @Transactional
    public CartResponse addItem(String email, CartItemRequest req) {
        User user = loadUser(email);
        Book book = bookRepository.findByIdAndIsDeletedFalse(req.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", req.getBookId().toString()));
        if (book.getStockQuantity() < req.getQuantity())
            throw new BusinessRuleException("CART_002", "Insufficient stock");

        Cart cart = getOrCreateCart(user);
        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(req.getBookId()) && !i.getIsDeleted())
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + req.getQuantity());
        } else {
            cart.getItems().add(CartItem.builder()
                    .cart(cart).book(book)
                    .quantity(req.getQuantity())
                    .unitPrice(book.getPrice())
                    .build());
        }
        return toCartResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse updateItem(String email, UUID bookId, int quantity) {
        User user = loadUser(email);
        Cart cart = getOrCreateCart(user);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(bookId) && !i.getIsDeleted())
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", bookId.toString()));
        item.setQuantity(quantity);
        return toCartResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse removeItem(String email, UUID bookId) {
        User user = loadUser(email);
        Cart cart = getOrCreateCart(user);
        cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(bookId))
                .forEach(CartItem::softDelete);
        return toCartResponse(cartRepository.save(cart));
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(String email) {
        User user = loadUser(email);
        Cart cart = cartRepository.findActiveCartByUserId(user.getId())
                .orElse(Cart.builder().user(user).status("ACTIVE").items(new ArrayList<>()).build());
        return toCartResponse(cart);
    }

    @Transactional
    public void clearCart(String email) {
        User user = loadUser(email);
        cartRepository.findActiveCartByUserId(user.getId())
                .ifPresent(c -> { c.softDelete(); cartRepository.save(c); });
    }

    // ── mapper ───────────────────────────────────────────────────────────────

    private CartResponse toCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .filter(i -> !i.getIsDeleted())
                .map(i -> CartItemResponse.builder()
                        .bookId(i.getBook().getId())
                        .book(bookService.toSummary(i.getBook()))
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .lineTotal(i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                        .build())
                .collect(Collectors.toList());

        BigDecimal subtotal = items.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .status(cart.getStatus())
                .items(items)
                .subtotal(subtotal)
                .itemCount(items.size())
                .recommendations(List.of())
                .build();
    }
}
