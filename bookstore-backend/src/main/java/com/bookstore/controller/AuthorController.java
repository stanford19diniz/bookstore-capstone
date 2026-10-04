package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.dto.AuthorDto.*;
import com.bookstore.entity.Author;
import com.bookstore.exception.*;
import com.bookstore.repository.AuthorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorRepository authorRepository;

    private AuthorResponse toResponse(Author a) {
        return AuthorResponse.builder()
                .id(a.getId()).firstName(a.getFirstName()).lastName(a.getLastName())
                .bio(a.getBio()).profileImageUrl(a.getProfileImageUrl()).build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuthorResponse>>> listAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                authorRepository.findAllByIsDeletedFalse(PageRequest.of(page, size)).map(this::toResponse)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorResponse>> getAuthorById(@PathVariable UUID id) {
        Author a = authorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", id.toString()));
        return ResponseEntity.ok(ApiResponse.success(toResponse(a)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AuthorResponse>> createAuthor(@Valid @RequestBody AuthorRequest req) {
        Author a = Author.builder().firstName(req.getFirstName()).lastName(req.getLastName())
                .bio(req.getBio()).profileImageUrl(req.getProfileImageUrl()).build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(toResponse(authorRepository.save(a))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorResponse>> updateAuthor(
            @PathVariable UUID id, @Valid @RequestBody AuthorRequest req) {
        Author a = authorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", id.toString()));
        a.setFirstName(req.getFirstName()); a.setLastName(req.getLastName());
        if (req.getBio() != null) a.setBio(req.getBio());
        if (req.getProfileImageUrl() != null) a.setProfileImageUrl(req.getProfileImageUrl());
        return ResponseEntity.ok(ApiResponse.success(toResponse(authorRepository.save(a))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteAuthor(@PathVariable UUID id) {
        Author a = authorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", id.toString()));
        a.softDelete();
        authorRepository.save(a);
        return ResponseEntity.ok(ApiResponse.message("Author deleted"));
    }
}
