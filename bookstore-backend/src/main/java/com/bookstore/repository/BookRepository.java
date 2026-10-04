package com.bookstore.repository;

import com.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {

    Optional<Book> findByIdAndIsDeletedFalse(UUID id);

    boolean existsByIsbnAndIsDeletedFalse(String isbn);

    @Query("""
        SELECT DISTINCT b FROM Book b
        LEFT JOIN b.categories c
        WHERE b.isDeleted = false
          AND b.isActive = true
          AND (:categoryId IS NULL OR c.id = :categoryId)
          AND (:publisherId IS NULL OR b.publisher.id = :publisherId)
          AND (:minPrice IS NULL OR b.price >= :minPrice)
          AND (:maxPrice IS NULL OR b.price <= :maxPrice)
          AND (:language IS NULL OR b.language = :language)
        """)
    Page<Book> findWithFilters(
            @Param("categoryId") UUID categoryId,
            @Param("publisherId") UUID publisherId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("language") String language,
            Pageable pageable);

    @Query("""
        SELECT DISTINCT b FROM Book b
        LEFT JOIN b.authors a
        LEFT JOIN b.categories c
        WHERE b.isDeleted = false AND b.isActive = true
          AND (
            LOWER(b.title) LIKE CONCAT('%', LOWER(:q), '%')
            OR LOWER(a.firstName) LIKE CONCAT('%', LOWER(:q), '%')
            OR LOWER(a.lastName)  LIKE CONCAT('%', LOWER(:q), '%')
            OR b.isbn LIKE CONCAT('%', :q, '%')
          )
          AND (:categoryId IS NULL OR c.id = :categoryId)
          AND (:authorId IS NULL OR a.id = :authorId)
          AND (:publisherId IS NULL OR b.publisher.id = :publisherId)
          AND (:minPrice IS NULL OR b.price >= :minPrice)
          AND (:maxPrice IS NULL OR b.price <= :maxPrice)
        """)
    Page<Book> searchBooks(
            @Param("q") String q,
            @Param("categoryId") UUID categoryId,
            @Param("authorId") UUID authorId,
            @Param("publisherId") UUID publisherId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    @Query("""
        SELECT DISTINCT b FROM Book b
        JOIN b.categories c
        WHERE b.isDeleted = false AND b.isActive = true
          AND c.id IN (SELECT c2.id FROM Book b2 JOIN b2.categories c2 WHERE b2.id = :bookId)
          AND b.id <> :bookId
        """)
    List<Book> findRelatedBooks(@Param("bookId") UUID bookId, Pageable pageable);
}
