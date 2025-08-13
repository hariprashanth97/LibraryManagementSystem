package com.example.library.controller;

import com.example.library.dto.BookResponse;
import com.example.library.dto.BookWithBorrowerResponse;
import com.example.library.dto.BorrowerResponse;
import com.example.library.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    void createBook_returnsCreatedBook_withoutBorrower() throws Exception {
        BookResponse mock = new BookResponse(1L, "ISBN-1", "Title A", "Author A");
        when(bookService.create(any())).thenReturn(mock);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "isbn":"ISBN-1",
                          "title":"Title A",
                          "author":"Author A"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.isbn").value("ISBN-1"))
                .andExpect(jsonPath("$.title").value("Title A"));
    }

    @Test
    void getAllBooks_returnsList_withBorrowerIfPresent() throws Exception {
        BorrowerResponse borrower = new BorrowerResponse(10L, "John", "john@x.com", null, null);
        BookWithBorrowerResponse b1 = new BookWithBorrowerResponse(1L, "I1", "T1", "A1", borrower);
        BookWithBorrowerResponse b2 = new BookWithBorrowerResponse(2L, "I2", "T2", "A2", null);

        when(bookService.getAllBooks()).thenReturn(List.of(b1, b2));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].borrower.id").value(10L))
                .andExpect(jsonPath("$[1].borrower").doesNotExist());
    }
}
