package com.example.library.controller;

import com.example.library.dto.BookResponse;
import com.example.library.dto.BorrowerResponse;
import com.example.library.dto.BorrowerWithBooksResponse;
import com.example.library.exception.BookAlreadyBorrowedException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.service.BorrowerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BorrowerController.class)
class BorrowerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BorrowerService borrowerService;

    @Test
    void registerBorrower_returnsBorrowerOnly() throws Exception {
        BorrowerResponse mock = new BorrowerResponse(1L, "John", "john@x.com", null, null);
        when(borrowerService.create(any())).thenReturn(mock);

        mockMvc.perform(post("/api/borrowers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name":"John",
                          "email":"john@x.com"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.book").doesNotExist());
    }

    @Test
    void getBorrowerById_returnsBorrowerWithBooks() throws Exception {
        BorrowerWithBooksResponse mock = new BorrowerWithBooksResponse(
                1L, "John", "john@x.com",
                List.of(new BookResponse(11L, "I1", "T1", "A1"))
        );
        when(borrowerService.findById(1L)).thenReturn(mock);

        mockMvc.perform(get("/api/borrowers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.books", hasSize(1)))
                .andExpect(jsonPath("$.books[0].isbn").value("I1"));
    }

    @Test
    void getBorrowerById_notFound_returns404() throws Exception {
        when(borrowerService.findById(99L)).thenThrow(new ResourceNotFoundException("not found"));

        mockMvc.perform(get("/borrowers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllBorrowers_returnsList() throws Exception {
        BorrowerWithBooksResponse b1 = new BorrowerWithBooksResponse(1L, "J1", "j1@x.com", List.of());
        BorrowerWithBooksResponse b2 = new BorrowerWithBooksResponse(2L, "J2", "j2@x.com", List.of());
        when(borrowerService.getAllBorrowers()).thenReturn(List.of(b1, b2));

        mockMvc.perform(get("/api/borrowers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void borrowBook_returnsBorrowerWithOnlyThatBook() throws Exception {
        BorrowerResponse mock = new BorrowerResponse(
                1L, "John", "john@x.com",
                "Book borrowed successfully",
                new BookResponse(11L, "I1", "T1", "A1")
        );
        when(borrowerService.borrow(1L, 11L)).thenReturn(mock);

        mockMvc.perform(post("/api/borrowers/1/borrow/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.bookResponse.id").value(11L))
                .andExpect(jsonPath("$.message").value("Book borrowed successfully"));
    }

    @Test
    void borrowBook_whenAlreadyBorrowed_returns409_or400() throws Exception {
        when(borrowerService.borrow(1L, 11L)).thenThrow(new BookAlreadyBorrowedException("already"));

        mockMvc.perform(post("/api/borrowers/1/borrow/11"))
                // depending on your @ControllerAdvice mapping; adjust if it’s 400/409
                .andExpect(status().isConflict());
    }

    @Test
    void returnBook_returnsBorrowerWithReturnedBook() throws Exception {
        BorrowerResponse mock = new BorrowerResponse(
                1L, "John", "john@x.com",
                "Book returned successfully",
                new BookResponse(11L, "I1", "T1", "A1")
        );
        when(borrowerService.returnBook(1L, 11L)).thenReturn(mock);

        mockMvc.perform(post("/api/borrowers/1/return/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookResponse.id").value(11L))
                .andExpect(jsonPath("$.message").value("Book returned successfully"));
    }
}
