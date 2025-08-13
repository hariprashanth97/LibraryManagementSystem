package com.example.library.service;

import com.example.library.dto.BorrowerResponse;
import com.example.library.dto.BorrowerWithBooksResponse;
import com.example.library.entity.Borrower;

import java.util.List;

public interface BorrowerService {

    BorrowerResponse create(Borrower borrower);

    BorrowerWithBooksResponse findById(Long id);

    List<BorrowerWithBooksResponse> getAllBorrowers();

    BorrowerResponse borrow(Long borrowerId, Long bookId);

    BorrowerResponse returnBook(Long borrowerId, Long bookId);
}
