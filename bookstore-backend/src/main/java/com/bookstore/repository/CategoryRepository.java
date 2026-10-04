package com.bookstore.repository;

import com.bookstore.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findAllByIsDeletedFalse();
    Optional<Category> findByIdAndIsDeletedFalse(UUID id);
    boolean existsBySlugAndIsDeletedFalse(String slug);
}
