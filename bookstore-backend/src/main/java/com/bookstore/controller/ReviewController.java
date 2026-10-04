package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.dto.ReviewDto.*;
import com.bookstore.entity.*;
import com.bookstore.exception.*;
import com.bookstore.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private ReviewResponse toResponse(Review r) {
        return ReviewResponse.builder()
                .id(r.getId()).bookId(r.getBook().getId())
                .userId(r.getUser().getId()).userFirstName(r.getUser().getFirstName())
                .rating(r.getRating()).comment(r.getComment())
                .status(r.getStatus()).createdAt(r.getCreatedAt()).build();
    }

    @GetMapping("/books/{bookId}")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> listBookReviews(
            @PathVariable UUID bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                reviewRepository.findByBookIdAndStatusAndIsDeletedFalse(bookId, "APPROVED",
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
                        .map(this::toResponse)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> submitReview(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody ReviewRequest req) {
        User user = userRepository.findByEmailAndIsDeletedFalse(principal.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", principal.getUsername()));
        Book book = bookRepository.findByIdAndIsDeletedFalse(req.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", req.getBookId().toString()));
        if (reviewRepository.existsByUserIdAndBookIdAndIsDeletedFalse(user.getId(), req.getBookId()))
            throw new ConflictException("REV_002", "You have already reviewed this book");
        Review review = Review.builder()
                .user(user).book(book).rating(req.getRating())
                .comment(req.getComment()).status("PENDING").build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(toResponse(reviewRepository.save(review))));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> myReviews(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User user = userRepository.findByEmailAndIsDeletedFalse(principal.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", principal.getUsername()));
        return ResponseEntity.ok(ApiResponse.success(
                reviewRepository.findByUserIdAndIsDeletedFalse(user.getId(),
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
                        .map(this::toResponse)));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<ReviewResponse>> approveReview(@PathVariable UUID id) {
        Review r = reviewRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id.toString()));
        r.setStatus("APPROVED");
        // update book average rating
        reviewRepository.save(r);
        updateBookRating(r.getBook().getId());
        return ResponseEntity.ok(ApiResponse.success(toResponse(r)));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<ReviewResponse>> rejectReview(@PathVariable UUID id) {
        Review r = reviewRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id.toString()));
        r.setStatus("REJECTED");
        return ResponseEntity.ok(ApiResponse.success(toResponse(reviewRepository.save(r))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteReview(@PathVariable UUID id) {
        Review r = reviewRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id.toString()));
        r.softDelete();
        reviewRepository.save(r);
        return ResponseEntity.ok(ApiResponse.message("Review deleted"));
    }

    private void updateBookRating(UUID bookId) {
        bookRepository.findByIdAndIsDeletedFalse(bookId).ifPresent(book -> {
            Double avg = reviewRepository.findAverageRatingByBookId(bookId);
            Long count = reviewRepository.countApprovedByBookId(bookId);
            book.setAverageRating(avg != null
                    ? java.math.BigDecimal.valueOf(avg).setScale(2, java.math.RoundingMode.HALF_UP)
                    : java.math.BigDecimal.ZERO);
            book.setReviewCount(count != null ? count.intValue() : 0);
            bookRepository.save(book);
        });
    }
}
