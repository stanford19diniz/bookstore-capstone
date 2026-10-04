package com.bookstore.service;

import com.bookstore.dto.BookDto.*;
import com.bookstore.entity.*;
import com.bookstore.exception.ConflictException;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Unit Tests")
class BookServiceTest {

    @Mock BookRepository bookRepository;
    @Mock AuthorRepository authorRepository;
    @Mock PublisherRepository publisherRepository;
    @Mock CategoryRepository categoryRepository;

    @InjectMocks BookService bookService;

    private Publisher publisher;
    private Author author;
    private Category category;
    private Book book;

    @BeforeEach
    void setUp() {
        publisher = Publisher.builder().name("Penguin").build();
        publisher.setId(UUID.randomUUID());

        author = Author.builder().firstName("George").lastName("Orwell").build();
        author.setId(UUID.randomUUID());

        category = Category.builder().name("Fiction").slug("fiction").build();
        category.setId(UUID.randomUUID());

        book = Book.builder()
                .title("1984").isbn("9780451524935").publisher(publisher)
                .price(new BigDecimal("299.00")).stockQuantity(100)
                .deliveryDays(3).language("English")
                .authors(Set.of(author)).categories(Set.of(category))
                .build();
        book.setId(UUID.randomUUID());
        book.setIsDeleted(false);
    }

    @Test
    @DisplayName("getById - returns book detail when found")
    void getById_found() {
        when(bookRepository.findByIdAndIsDeletedFalse(book.getId()))
                .thenReturn(Optional.of(book));

        BookDetailResponse result = bookService.getById(book.getId());

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("1984");
        assertThat(result.getIsbn()).isEqualTo("9780451524935");
        assertThat(result.getPrice()).isEqualByComparingTo("299.00");
    }

    @Test
    @DisplayName("getById - throws ResourceNotFoundException when not found")
    void getById_notFound() {
        UUID randomId = UUID.randomUUID();
        when(bookRepository.findByIdAndIsDeletedFalse(randomId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getById(randomId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("create - success creates and returns book")
    void create_success() {
        BookRequest req = BookRequest.builder()
                .title("1984").isbn("9780451524935")
                .publisherId(publisher.getId()).price(new BigDecimal("299.00"))
                .stockQuantity(100).deliveryDays(3).language("English")
                .authorIds(List.of(author.getId()))
                .categoryIds(List.of(category.getId())).build();

        when(bookRepository.existsByIsbnAndIsDeletedFalse("9780451524935")).thenReturn(false);
        when(publisherRepository.findByIdAndIsDeletedFalse(publisher.getId()))
                .thenReturn(Optional.of(publisher));
        when(authorRepository.findAllById(any())).thenReturn(List.of(author));
        when(categoryRepository.findAllById(any())).thenReturn(List.of(category));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookDetailResponse result = bookService.create(req);

        assertThat(result.getTitle()).isEqualTo("1984");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("create - throws ConflictException for duplicate ISBN")
    void create_duplicateIsbn_throwsConflict() {
        BookRequest req = BookRequest.builder()
                .title("1984").isbn("9780451524935")
                .publisherId(publisher.getId()).price(new BigDecimal("299.00"))
                .stockQuantity(100).deliveryDays(3).language("English")
                .authorIds(List.of(author.getId()))
                .categoryIds(List.of(category.getId())).build();

        when(bookRepository.existsByIsbnAndIsDeletedFalse("9780451524935")).thenReturn(true);

        assertThatThrownBy(() -> bookService.create(req))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("ISBN already exists");
    }

    @Test
    @DisplayName("delete - soft deletes book")
    void delete_softDeletes() {
        when(bookRepository.findByIdAndIsDeletedFalse(book.getId()))
                .thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        bookService.delete(book.getId());

        assertThat(book.getIsDeleted()).isTrue();
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("listBooks - returns paginated results")
    void listBooks_returnsPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findWithFilters(null, null, null, null, null, pageable))
                .thenReturn(page);

        Page<BookSummaryResponse> result = bookService.listBooks(null, null, null, null, null, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("1984");
    }
}
