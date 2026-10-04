package com.bookstore.repository;

import com.bookstore.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
    Page<Author> findAllByIsDeletedFalse(Pageable pageable);
    Optional<Author> findByIdAndIsDeletedFalse(UUID id);
}
