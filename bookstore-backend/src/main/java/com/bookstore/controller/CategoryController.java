package com.bookstore.controller;

import com.bookstore.common.ApiResponse;
import com.bookstore.dto.CategoryDto.*;
import com.bookstore.entity.Category;
import com.bookstore.exception.*;
import com.bookstore.repository.CategoryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    private CategoryResponse toResponse(Category c) {
        return CategoryResponse.builder()
                .id(c.getId()).name(c.getName()).slug(c.getSlug())
                .parentId(c.getParent() != null ? c.getParent().getId() : null)
                .displayOrder(c.getDisplayOrder())
                .build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> listCategories() {
        List<CategoryResponse> list = categoryRepository.findAllByIsDeletedFalse()
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable UUID id) {
        Category cat = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id.toString()));
        return ResponseEntity.ok(ApiResponse.success(toResponse(cat)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest req) {
        if (categoryRepository.existsBySlugAndIsDeletedFalse(req.getSlug()))
            throw new ConflictException("CAT_001", "Slug already exists: " + req.getSlug());
        Category parent = req.getParentId() != null
                ? categoryRepository.findByIdAndIsDeletedFalse(req.getParentId()).orElse(null) : null;
        Category cat = Category.builder()
                .name(req.getName()).slug(req.getSlug()).parent(parent)
                .displayOrder(req.getDisplayOrder() != null ? req.getDisplayOrder() : 0).build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(toResponse(categoryRepository.save(cat))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable UUID id, @Valid @RequestBody CategoryRequest req) {
        Category cat = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id.toString()));
        cat.setName(req.getName()); cat.setSlug(req.getSlug());
        if (req.getDisplayOrder() != null) cat.setDisplayOrder(req.getDisplayOrder());
        return ResponseEntity.ok(ApiResponse.success(toResponse(categoryRepository.save(cat))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteCategory(@PathVariable UUID id) {
        Category cat = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id.toString()));
        cat.softDelete();
        categoryRepository.save(cat);
        return ResponseEntity.ok(ApiResponse.message("Category deleted"));
    }
}
