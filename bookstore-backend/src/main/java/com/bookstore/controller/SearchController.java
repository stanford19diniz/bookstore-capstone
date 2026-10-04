package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.dto.BookDto.BookSummaryResponse;
import com.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final BookService bookService;

    @GetMapping("/books")
    public ResponseEntity<ApiResponse<Page<BookSummaryResponse>>> search(
            @RequestParam String q,
            @RequestParam(required=false) UUID categoryId,
            @RequestParam(required=false) UUID authorId,
            @RequestParam(required=false) UUID publisherId,
            @RequestParam(required=false) BigDecimal minPrice,
            @RequestParam(required=false) BigDecimal maxPrice,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size,
            @RequestParam(defaultValue="title,asc") String sort) {
        String[] s = sort.split(",");
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(s.length > 1 && "desc".equalsIgnoreCase(s[1])
                        ? Sort.Direction.DESC : Sort.Direction.ASC, s[0]));
        return ResponseEntity.ok(ApiResponse.success(
                bookService.search(q, categoryId, authorId, publisherId, minPrice, maxPrice, pageable)));
    }
}
