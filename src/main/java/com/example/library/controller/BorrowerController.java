package com.example.library.controller;

import com.example.library.dto.BorrowerRequest;
import com.example.library.dto.BorrowerResponse;
import com.example.library.dto.BorrowerWithBooksResponse;
import com.example.library.entity.Borrower;
import com.example.library.service.BorrowerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing borrower operations such as
 *  registration, borrowing, returning, and fetching borrower details.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/borrowers")
public class BorrowerController {

    private static final Logger logger = LoggerFactory.getLogger(BorrowerController.class);

    private final BorrowerService borrowerService;


    // Register a new borrower.
    @PostMapping
    public ResponseEntity<BorrowerResponse> create(@Valid @RequestBody BorrowerRequest request) {
        logger.info("Registering new borrower: {}", request);

        Borrower borrower = new Borrower(request.name(), request.email());
        BorrowerResponse savedBorrower = borrowerService.create(borrower);

        logger.info("Borrower registered successfully: ID={}", savedBorrower.id());
        return ResponseEntity.status(201).body(savedBorrower);
    }


    //Borrow a book for a specific borrower.
    @PostMapping("/{borrowerId}/borrow/{bookId}")
    public ResponseEntity<BorrowerResponse> borrow(@PathVariable Long borrowerId, @PathVariable Long bookId) {
        logger.info("Borrower ID={} is attempting to borrow book ID={}", borrowerId, bookId);

        BorrowerResponse borrowedBook = borrowerService.borrow(borrowerId, bookId);

        logger.info("Book borrowed successfully");
        return ResponseEntity.ok(borrowedBook);
    }

    //Return a borrowed book.
    @PostMapping("/{borrowerId}/return/{bookId}")
    public ResponseEntity<BorrowerResponse> returnBook(@PathVariable Long borrowerId, @PathVariable Long bookId) {
        logger.info("Borrower ID={} is returning book ID={}", borrowerId, bookId);

        BorrowerResponse returnedBook = borrowerService.returnBook(borrowerId, bookId);

        logger.info("Book returned successfully");
        return ResponseEntity.ok(returnedBook);
    }

    //Get borrower details by ID.
    @GetMapping("/{id}")
    public ResponseEntity<BorrowerWithBooksResponse> getBorrowersById(@PathVariable Long id) {
        logger.info("Fetching borrower with ID={}", id);

        BorrowerWithBooksResponse borrower = borrowerService.findById(id);
        return ResponseEntity.ok(borrower);
    }


    //Get a list of all borrowers.
    @GetMapping
    public ResponseEntity<List<BorrowerWithBooksResponse>> getAllBorrowers() {
        logger.info("Fetching all borrowers");

        List<BorrowerWithBooksResponse> borrowers = borrowerService.getAllBorrowers();
        logger.info("Found {} borrowers", borrowers.size());

        return ResponseEntity.ok(borrowers);
    }
}
