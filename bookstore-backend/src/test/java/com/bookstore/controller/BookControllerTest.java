package com.bookstore.controller;

import com.bookstore.dto.BookDto.*;
import com.bookstore.exception.GlobalExceptionHandler;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = BookController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
@Import(GlobalExceptionHandler.class)
@DisplayName("BookController Unit Tests")
class BookControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean BookService bookService;
    @MockBean JpaMetamodelMappingContext jpaMetamodelMappingContext;
    @MockBean com.bookstore.security.JwtProvider jwtProvider;
    @MockBean org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private BookSummaryResponse sampleSummary() {
        return BookSummaryResponse.builder()
                .id(UUID.randomUUID()).title("1984").isbn("9780451524935")
                .price(new BigDecimal("299.00")).deliveryDays(3)
                .averageRating(BigDecimal.ZERO).reviewCount(0)
                .authors(List.of()).categories(List.of()).build();
    }

    private BookDetailResponse sampleDetail() {
        return BookDetailResponse.builder()
                .id(UUID.randomUUID()).title("1984").isbn("9780451524935")
                .price(new BigDecimal("299.00")).deliveryDays(3)
                .averageRating(BigDecimal.ZERO).reviewCount(0)
                .authors(List.of()).categories(List.of())
                .language("English").stockQuantity(100).isActive(true).build();
    }

    @Test
    @DisplayName("GET /api/v1/books - returns 200 with paginated list")
    void listBooks_returns200() throws Exception {
        Page<BookSummaryResponse> page = new PageImpl<>(List.of(sampleSummary()));
        when(bookService.listBooks(any(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].title").value("1984"));
    }

    @Test
    @DisplayName("GET /api/v1/books/{id} - returns 200 when book found")
    void getBook_found_returns200() throws Exception {
        BookDetailResponse detail = sampleDetail();
        when(bookService.getById(detail.getId())).thenReturn(detail);

        mockMvc.perform(get("/api/v1/books/{id}", detail.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("1984"));
    }

    @Test
    @DisplayName("GET /api/v1/books/{id} - returns 404 when not found")
    void getBook_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(bookService.getById(id)).thenThrow(new ResourceNotFoundException("Book", id.toString()));

        mockMvc.perform(get("/api/v1/books/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/books/{id}/related - returns 200")
    void relatedBooks_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(bookService.getRelatedBooks(id)).thenReturn(List.of(sampleSummary()));

        mockMvc.perform(get("/api/v1/books/{id}/related", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("1984"));
    }
}
