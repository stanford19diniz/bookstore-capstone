package com.bookstore.service;

import com.bookstore.dto.BookDto.*;
import com.bookstore.dto.AuthorDto.AuthorResponse;
import com.bookstore.dto.CategoryDto.CategoryResponse;
import com.bookstore.dto.PublisherDto.PublisherResponse;
import com.bookstore.entity.*;
import com.bookstore.exception.*;
import com.bookstore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final CategoryRepository categoryRepository;

    public Page<BookSummaryResponse> listBooks(UUID categoryId, UUID publisherId,
                                                BigDecimal minPrice, BigDecimal maxPrice,
                                                String language, Pageable pageable) {
        return bookRepository.findWithFilters(categoryId, publisherId, minPrice, maxPrice, language, pageable)
                .map(this::toSummary);
    }

    public BookDetailResponse getById(UUID id) {
        Book book = bookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id.toString()));
        return toDetail(book);
    }

    public Page<BookSummaryResponse> search(String q, UUID categoryId, UUID authorId,
                                             UUID publisherId, BigDecimal minPrice,
                                             BigDecimal maxPrice, Pageable pageable) {
        return bookRepository.searchBooks(q, categoryId, authorId, publisherId, minPrice, maxPrice, pageable)
                .map(this::toSummary);
    }

    @Transactional
    public BookDetailResponse create(BookRequest req) {
        if (bookRepository.existsByIsbnAndIsDeletedFalse(req.getIsbn())) {
            throw new ConflictException("BOOK_002", "ISBN already exists: " + req.getIsbn());
        }
        Publisher publisher = publisherRepository.findByIdAndIsDeletedFalse(req.getPublisherId())
                .orElseThrow(() -> new ResourceNotFoundException("Publisher", req.getPublisherId().toString()));

        Set<Author> authors = new HashSet<>(authorRepository.findAllById(req.getAuthorIds()));
        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(req.getCategoryIds()));

        Book book = Book.builder()
                .title(req.getTitle()).isbn(req.getIsbn()).publisher(publisher)
                .description(req.getDescription()).price(req.getPrice())
                .stockQuantity(req.getStockQuantity()).coverImageUrl(req.getCoverImageUrl())
                .language(req.getLanguage() != null ? req.getLanguage() : "English")
                .pages(req.getPages()).publishedDate(req.getPublishedDate())
                .deliveryDays(req.getDeliveryDays() != null ? req.getDeliveryDays() : 5)
                .authors(authors).categories(categories).build();
        return toDetail(bookRepository.save(book));
    }

    @Transactional
    public BookDetailResponse update(UUID id, BookRequest req) {
        Book book = bookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id.toString()));
        book.setTitle(req.getTitle()); book.setPrice(req.getPrice());
        book.setDescription(req.getDescription()); book.setStockQuantity(req.getStockQuantity());
        book.setCoverImageUrl(req.getCoverImageUrl()); book.setDeliveryDays(req.getDeliveryDays());
        if (req.getAuthorIds() != null)
            book.setAuthors(new HashSet<>(authorRepository.findAllById(req.getAuthorIds())));
        if (req.getCategoryIds() != null)
            book.setCategories(new HashSet<>(categoryRepository.findAllById(req.getCategoryIds())));
        return toDetail(bookRepository.save(book));
    }

    @Transactional
    public void delete(UUID id) {
        Book book = bookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id.toString()));
        book.softDelete();
        bookRepository.save(book);
    }

    public List<BookSummaryResponse> getRelatedBooks(UUID id) {
        return bookRepository.findRelatedBooks(id, PageRequest.of(0, 6))
                .stream().map(this::toSummary).collect(Collectors.toList());
    }

    public BookSummaryResponse toSummary(Book b) {
        return BookSummaryResponse.builder()
                .id(b.getId()).title(b.getTitle()).isbn(b.getIsbn())
                .price(b.getPrice()).coverImageUrl(b.getCoverImageUrl())
                .averageRating(b.getAverageRating()).reviewCount(b.getReviewCount())
                .deliveryDays(b.getDeliveryDays())
                .authors(b.getAuthors().stream().map(a -> AuthorResponse.builder()
                        .id(a.getId()).firstName(a.getFirstName()).lastName(a.getLastName())
                        .bio(a.getBio()).profileImageUrl(a.getProfileImageUrl()).build())
                        .collect(Collectors.toList()))
                .categories(b.getCategories().stream().map(c -> CategoryResponse.builder()
                        .id(c.getId()).name(c.getName()).slug(c.getSlug())
                        .displayOrder(c.getDisplayOrder()).build())
                        .collect(Collectors.toList()))
                .build();
    }

    private BookDetailResponse toDetail(Book b) {
        BookSummaryResponse s = toSummary(b);
        return BookDetailResponse.builder()
                .id(s.getId()).title(s.getTitle()).isbn(s.getIsbn())
                .price(s.getPrice()).coverImageUrl(s.getCoverImageUrl())
                .averageRating(s.getAverageRating()).reviewCount(s.getReviewCount())
                .deliveryDays(s.getDeliveryDays()).authors(s.getAuthors()).categories(s.getCategories())
                .description(b.getDescription())
                .publisher(b.getPublisher() != null ? PublisherResponse.builder()
                        .id(b.getPublisher().getId()).name(b.getPublisher().getName())
                        .country(b.getPublisher().getCountry()).build() : null)
                .language(b.getLanguage()).pages(b.getPages())
                .publishedDate(b.getPublishedDate()).stockQuantity(b.getStockQuantity())
                .isActive(b.getIsActive()).build();
    }
}
