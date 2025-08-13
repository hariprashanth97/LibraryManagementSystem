package com.example.library.controller;

import com.example.library.dto.BookRequest;
import com.example.library.dto.BookResponse;
import com.example.library.dto.BookWithBorrowerResponse;
import com.example.library.entity.Book;
import com.example.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for handling book-related operations.
 * Provides endpoints to register a new book and retrieve all books.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;

    //Registers a new book in the system.
    @PostMapping
    public ResponseEntity<BookResponse> registerBook(@Valid @RequestBody BookRequest req) {
        logger.info("Received request to register a book: ISBN={}, Title={}, Author={}",
                req.isbn(), req.title(), req.author());

        Book book = new Book(req.isbn(), req.title(), req.author());
        BookResponse savedBook = bookService.create(book);

        logger.info("Book successfully registered: ID={}, ISBN={}", savedBook.id(), savedBook.isbn());

        return ResponseEntity.status(201).body(savedBook);
    }


    //Retrieves all books in the system.
    @GetMapping
    public ResponseEntity<List<BookWithBorrowerResponse>> getAllBooks() {
        logger.info("📖 Request received to fetch all books");

        List<BookWithBorrowerResponse> books = bookService.getAllBooks();

        logger.info("Retrieved {} books from the database", books.size());

        return ResponseEntity.ok(books);
    }
}
