package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.dto.BookDto.*;
import com.bookstore.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookSummaryResponse>>> listBooks(
            @RequestParam(required=false) UUID categoryId,
            @RequestParam(required=false) UUID publisherId,
            @RequestParam(required=false) BigDecimal minPrice,
            @RequestParam(required=false) BigDecimal maxPrice,
            @RequestParam(required=false) String language,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size,
            @RequestParam(defaultValue="title,asc") String sort) {
        String[] s = sort.split(",");
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(s.length > 1 && "desc".equalsIgnoreCase(s[1])
                        ? Sort.Direction.DESC : Sort.Direction.ASC, s[0]));
        return ResponseEntity.ok(ApiResponse.success(
                bookService.listBooks(categoryId, publisherId, minPrice, maxPrice, language, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDetailResponse>> getBook(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(bookService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookDetailResponse>> createBook(@Valid @RequestBody BookRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(bookService.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDetailResponse>> updateBook(
            @PathVariable UUID id, @Valid @RequestBody BookRequest req) {
        return ResponseEntity.ok(ApiResponse.success(bookService.update(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteBook(@PathVariable UUID id) {
        bookService.delete(id);
        return ResponseEntity.ok(ApiResponse.message("Book deleted"));
    }

    @GetMapping("/{id}/related")
    public ResponseEntity<ApiResponse<List<BookSummaryResponse>>> relatedBooks(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(bookService.getRelatedBooks(id)));
    }
}
