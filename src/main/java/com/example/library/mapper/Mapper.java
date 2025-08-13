package com.example.library.mapper;

import com.example.library.dto.BookResponse;
import com.example.library.dto.BookWithBorrowerResponse;
import com.example.library.dto.BorrowerResponse;
import com.example.library.dto.BorrowerWithBooksResponse;
import com.example.library.entity.Book;
import com.example.library.entity.Borrower;

import java.util.List;

public class Mapper {

    public static BookResponse toBookResponse(Book book) {
        return new BookResponse(book.getId(), book.getIsbn(), book.getTitle(), book.getAuthor());
    }

    public static BookWithBorrowerResponse toBookWithBorrowerResponse(Book book) {
        Borrower borrower = book.getBorrower();
        BorrowerResponse borrowerResponse = borrower != null
                ? new BorrowerResponse(borrower.getId(), borrower.getName(), borrower.getEmail(),null,null)
                : null;
        return new BookWithBorrowerResponse(book.getId(), book.getIsbn(), book.getTitle(), book.getAuthor(), borrowerResponse);
    }

    public static BorrowerResponse toBorrowerResponse(Borrower borrower,BookResponse bookResponse,String message) {
        return new BorrowerResponse(borrower.getId(), borrower.getName(), borrower.getEmail(),message,bookResponse);
    }

    public static BorrowerWithBooksResponse toBorrowerWithBooksResponse(Borrower borrower) {
        List<BookResponse> books = borrower.getBooks().stream()
                .map(Mapper::toBookResponse)
                .toList();
        return new BorrowerWithBooksResponse(borrower.getId(), borrower.getName(), borrower.getEmail(), books);
    }
}