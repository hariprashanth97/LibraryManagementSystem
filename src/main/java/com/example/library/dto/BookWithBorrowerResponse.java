package com.example.library.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookWithBorrowerResponse(Long id, String isbn, String title, String author,
                                       BorrowerResponse borrower) {}


