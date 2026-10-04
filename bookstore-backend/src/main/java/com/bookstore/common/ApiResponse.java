package com.bookstore.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Standard API response envelope.
 * All endpoints return: { "data": ..., "meta": {...}, "errors": [] }
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private T data;
    private Meta meta;

    @Builder.Default
    private List<Object> errors = Collections.emptyList();

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .data(data)
                .meta(Meta.now())
                .build();
    }

    public static ApiResponse<Object> message(String message) {
        return ApiResponse.builder()
                .data(java.util.Map.of("message", message))
                .meta(Meta.now())
                .build();
    }

    @Getter
    @Builder
    public static class Meta {
        private Instant timestamp;
        private String requestId;

        // Pagination fields (only set for paged responses)
        private Integer page;
        private Integer size;
        private Long totalElements;
        private Integer totalPages;

        public static Meta now() {
            return Meta.builder()
                    .timestamp(Instant.now())
                    .requestId(UUID.randomUUID().toString())
                    .build();
        }

        public static Meta paged(Instant timestamp, String requestId,
                                  int page, int size, long totalElements, int totalPages) {
            return Meta.builder()
                    .timestamp(timestamp)
                    .requestId(requestId)
                    .page(page)
                    .size(size)
                    .totalElements(totalElements)
                    .totalPages(totalPages)
                    .build();
        }
    }
}
