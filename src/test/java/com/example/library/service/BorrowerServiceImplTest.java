package com.example.library.service;

import com.example.library.dto.BookResponse;
import com.example.library.dto.BorrowerResponse;
import com.example.library.dto.BorrowerWithBooksResponse;
import com.example.library.entity.Book;
import com.example.library.entity.Borrower;
import com.example.library.exception.*;
import com.example.library.mapper.Mapper;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowerRepository;
import com.example.library.service.impl.BorrowerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BorrowerServiceImplTest {

    @Mock
    private BorrowerRepository borrowerRepository;
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BorrowerServiceImpl borrowerService;

    private Borrower borrower;
    private Book book;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        borrower = new Borrower();
        borrower.setId(1L);
        borrower.setName("John");

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setIsbn("12345");
        book.setAuthor("Author");
    }

    @Test
    void create_success() {
        when(borrowerRepository.save(borrower)).thenReturn(borrower);
        try (MockedStatic<Mapper> mocked = mockStatic(Mapper.class)) {
            BorrowerResponse mockResponse = new BorrowerResponse(1L, "John", "john@example.com", null, null);
            mocked.when(() -> Mapper.toBorrowerResponse(eq(borrower), isNull(), isNull()))
                    .thenReturn(mockResponse);

            BorrowerResponse response = borrowerService.create(borrower);

            assertEquals("John", response.name());
        }
    }


    @Test
    void findById_success() {
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));
        try (MockedStatic<Mapper> mocked = mockStatic(Mapper.class)) {
            BorrowerWithBooksResponse mockResponse = new BorrowerWithBooksResponse(1L, "John", "John@test.com",null);
            mocked.when(() -> Mapper.toBorrowerWithBooksResponse(borrower)).thenReturn(mockResponse);

            BorrowerWithBooksResponse response = borrowerService.findById(1L);

            assertEquals("John", response.name());
        }
    }

    @Test
    void findById_notFound() {
        when(borrowerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> borrowerService.findById(1L));
    }

    @Test
    void getAllBorrowers_success() {
        when(borrowerRepository.findAll()).thenReturn(List.of(borrower));
        try (MockedStatic<Mapper> mocked = mockStatic(Mapper.class)) {
            mocked.when(() -> Mapper.toBorrowerWithBooksResponse(borrower))
                    .thenReturn(new BorrowerWithBooksResponse(1L, "John", "John@test.com",null));
            List<BorrowerWithBooksResponse> result = borrowerService.getAllBorrowers();
            assertEquals(1, result.size());
        }
    }

    @Test
    void borrow_success() {
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        try (MockedStatic<Mapper> mocked = mockStatic(Mapper.class)) {
            mocked.when(() -> Mapper.toBookResponse(book)).thenReturn(new BookResponse(1L, "12345", "Test Book", "Author"));
            mocked.when(() -> Mapper.toBorrowerResponse(eq(borrower), any(BookResponse.class),anyString()))
                    .thenReturn(new BorrowerResponse(1L, "John", null, "Book Borrowed Successfully",null));

            BorrowerResponse response = borrowerService.borrow(1L, 1L);
            assertEquals("John", response.name());
            assertEquals("Book Borrowed Successfully",response.message());

        }
    }

    @Test
    void borrow_bookAlreadyBorrowed() {
        book.setBorrower(new Borrower());
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThrows(BookAlreadyBorrowedException.class, () -> borrowerService.borrow(1L, 1L));
    }

    @Test
    void borrow_conflict() {
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenThrow(new OptimisticLockingFailureException("err"));

        assertThrows(ConflictException.class, () -> borrowerService.borrow(1L, 1L));
    }

    @Test
    void returnBook_success() {
        book.setBorrower(borrower);
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        try (MockedStatic<Mapper> mocked = mockStatic(Mapper.class)) {
            mocked.when(() -> Mapper.toBookResponse(book)).thenReturn(new BookResponse(1L, "12345", "Test Book", "Author"));
            mocked.when(() -> Mapper.toBorrowerResponse(eq(borrower), any(BookResponse.class),anyString()))
                    .thenReturn(new BorrowerResponse(1L, "John", null, "Book Returned Successfully",null));

            BorrowerResponse response = borrowerService.returnBook(1L, 1L);
            assertEquals("John", response.name());
            assertEquals("Book Returned Successfully",response.message());

        }
    }

    @Test
    void returnBook_alreadyReturned() {
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book)); // no borrower set

        assertThrows(BookNotBorrowedException.class, () -> borrowerService.returnBook(1L, 1L));
    }
}
