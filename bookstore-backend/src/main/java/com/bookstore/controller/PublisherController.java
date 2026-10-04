package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.dto.PublisherDto.*;
import com.bookstore.entity.Publisher;
import com.bookstore.exception.*;
import com.bookstore.repository.PublisherRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherRepository publisherRepository;

    private PublisherResponse toResponse(Publisher p) {
        return PublisherResponse.builder()
                .id(p.getId()).name(p.getName())
                .country(p.getCountry()).websiteUrl(p.getWebsiteUrl()).build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PublisherResponse>>> listPublishers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                publisherRepository.findAllByIsDeletedFalse(PageRequest.of(page, size)).map(this::toResponse)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PublisherResponse>> getPublisherById(@PathVariable UUID id) {
        Publisher p = publisherRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher", id.toString()));
        return ResponseEntity.ok(ApiResponse.success(toResponse(p)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PublisherResponse>> createPublisher(@Valid @RequestBody PublisherRequest req) {
        if (publisherRepository.existsByNameAndIsDeletedFalse(req.getName()))
            throw new ConflictException("PUB_001", "Publisher already exists: " + req.getName());
        Publisher p = Publisher.builder().name(req.getName())
                .country(req.getCountry()).websiteUrl(req.getWebsiteUrl()).build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(toResponse(publisherRepository.save(p))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PublisherResponse>> updatePublisher(
            @PathVariable UUID id, @Valid @RequestBody PublisherRequest req) {
        Publisher p = publisherRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher", id.toString()));
        p.setName(req.getName());
        if (req.getCountry() != null) p.setCountry(req.getCountry());
        if (req.getWebsiteUrl() != null) p.setWebsiteUrl(req.getWebsiteUrl());
        return ResponseEntity.ok(ApiResponse.success(toResponse(publisherRepository.save(p))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deletePublisher(@PathVariable UUID id) {
        Publisher p = publisherRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher", id.toString()));
        p.softDelete();
        publisherRepository.save(p);
        return ResponseEntity.ok(ApiResponse.message("Publisher deleted"));
    }
}
