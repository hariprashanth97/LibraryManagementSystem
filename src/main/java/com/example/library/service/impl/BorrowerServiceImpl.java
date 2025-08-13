package com.example.library.service.impl;

import com.example.library.dto.BookResponse;
import com.example.library.dto.BorrowerResponse;
import com.example.library.dto.BorrowerWithBooksResponse;
import com.example.library.entity.Book;
import com.example.library.entity.Borrower;
import com.example.library.exception.*;
import com.example.library.mapper.Mapper;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowerRepository;
import com.example.library.service.BorrowerService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BorrowerServiceImpl implements BorrowerService {

    private final BorrowerRepository borrowerRepository;
    private final BookRepository bookRepository;

    public BorrowerServiceImpl(BorrowerRepository borrowerRepository, BookRepository bookRepository) {
        this.borrowerRepository = borrowerRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Register a new borrower — return borrower only, no books.
     */
    @Override
    public BorrowerResponse create(Borrower borrower) {
        Borrower saved = borrowerRepository.save(borrower);
        return Mapper.toBorrowerResponse(saved, null,null);
    }

    /**
     * Get borrower by ID — return with their books.
     */
    @Override
    public BorrowerWithBooksResponse findById(Long id) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id " + id));
        return Mapper.toBorrowerWithBooksResponse(borrower);
    }

    /**
     * Get all borrowers — with their books.
     */
    @Override
    public List<BorrowerWithBooksResponse> getAllBorrowers() {
        return borrowerRepository.findAll().stream()
                .map(Mapper::toBorrowerWithBooksResponse)
                .toList();
    }

    /**
     * Borrow a book — return borrower with only that book.
     */
    @Override
    public BorrowerResponse borrow(Long borrowerId, Long bookId) {
        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (book.getBorrower() != null) {
            throw new BookAlreadyBorrowedException("Book already borrowed");
        }

        book.setBorrower(borrower);

        try {
            Book borrowedBook = bookRepository.save(book);
            BookResponse bookResponse = Mapper.toBookResponse(borrowedBook);
            return Mapper.toBorrowerResponse(borrower,bookResponse,"Book Borrowed Successfully");

        } catch (OptimisticLockingFailureException ex) {
            throw new ConflictException("Concurrent modification while borrowing");
        }
    }

    /**
     * Return a book — return borrower with only that returned book info.
     */
    @Override
    public BorrowerResponse returnBook(Long borrowerId, Long bookId) {
        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (book.getBorrower() == null) {
            throw new BookNotBorrowedException("This book has not been borrowed yet");
        }

        if (!book.getBorrower().getId().equals(borrowerId)) {
            throw new BookAlreadyReturnedException("This book is borrowed by another user or already returned");
        }

        book.setBorrower(null);
        Book returnedBook = bookRepository.save(book);
        BookResponse bookResponse = Mapper.toBookResponse(returnedBook);

        return Mapper.toBorrowerResponse(borrower,bookResponse,"Book Returned Successfully");
    }
}
