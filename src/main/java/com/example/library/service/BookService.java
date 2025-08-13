package com.example.library.service;

import com.example.library.dto.BookResponse;
import com.example.library.dto.BookWithBorrowerResponse;
import com.example.library.entity.Book;

import java.util.List;

public interface BookService {
     BookResponse create(Book book);
     List<BookWithBorrowerResponse> getAllBooks();
}
