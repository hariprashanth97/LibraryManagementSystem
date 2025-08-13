package com.example.library.service;

import com.example.library.dto.BookResponse;
import com.example.library.dto.BookWithBorrowerResponse;
import com.example.library.entity.Book;
import com.example.library.exception.DuplicateIsbnException;
import com.example.library.mapper.Mapper;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowerRepository;
import com.example.library.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        book = new Book();
        book.setId(1L);
        book.setIsbn("12345");
        book.setTitle("Title");
        book.setAuthor("Author");
    }

    @Test
    void create_success() {
        when(bookRepository.findByIsbn("12345")).thenReturn(List.of());
        when(bookRepository.save(book)).thenReturn(book);

        try (MockedStatic<Mapper> mocked = mockStatic(Mapper.class)) {
            mocked.when(() -> Mapper.toBookResponse(book)).thenReturn(new BookResponse(1L, "12345", "Title", "Author"));
            BookResponse response = bookService.create(book);
            assertEquals("Title", response.title());
        }
    }

    @Test
    void create_duplicateIsbnMismatch() {
        Book otherBook = new Book();
        otherBook.setIsbn("12345");
        otherBook.setTitle("Different");
        otherBook.setAuthor("Other Author");
        when(bookRepository.findByIsbn("12345")).thenReturn(List.of(otherBook));

        assertThrows(DuplicateIsbnException.class, () -> bookService.create(book));
    }

    @Test
    void getAllBooks_success() {
        when(bookRepository.findAll()).thenReturn(List.of(book));
        try (MockedStatic<Mapper> mocked = mockStatic(Mapper.class)) {
            mocked.when(() -> Mapper.toBookWithBorrowerResponse(book))
                    .thenReturn(new BookWithBorrowerResponse(1L, "12345", "Title", "Author", null));
            List<BookWithBorrowerResponse> result = bookService.getAllBooks();
            assertEquals(1, result.size());
        }
    }
}
