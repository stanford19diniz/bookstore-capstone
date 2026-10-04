package com.bookstore.repository;

import com.bookstore.entity.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PublisherRepository extends JpaRepository<Publisher, UUID> {
    Page<Publisher> findAllByIsDeletedFalse(Pageable pageable);
    Optional<Publisher> findByIdAndIsDeletedFalse(UUID id);
    boolean existsByNameAndIsDeletedFalse(String name);
}
