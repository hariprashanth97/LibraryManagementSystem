package com.example.library.service.impl;

import com.example.library.dto.BookResponse;
import com.example.library.dto.BookWithBorrowerResponse;
import com.example.library.entity.Book;
import com.example.library.exception.DuplicateIsbnException;
import com.example.library.mapper.Mapper;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowerRepository;
import com.example.library.service.BookService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;

    public BookServiceImpl(BookRepository bookRepository, BorrowerRepository borrowerRepository) {
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
    }

    /**
     * Register a new book — only return the registered book (no borrower info).
     */
    @Override
    public BookResponse create(Book book) {
        List<Book> sameIsbn = bookRepository.findByIsbn(book.getIsbn());

        boolean mismatchExists = sameIsbn.stream().anyMatch(existing ->
                !existing.getTitle().equals(book.getTitle()) ||
                        !existing.getAuthor().equals(book.getAuthor()));

        if (mismatchExists) {
            throw new DuplicateIsbnException("ISBN already exists with different title/author");
        }

        Book createdBook = bookRepository.save(book);
        return Mapper.toBookResponse(createdBook); // no borrower
    }

    /**
     * Get all books with borrower info (if any).
     */
    @Override
    public List<BookWithBorrowerResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(Mapper::toBookWithBorrowerResponse)
                .toList();
    }
}
