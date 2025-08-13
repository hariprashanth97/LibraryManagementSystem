package com.example.library.dto;

import com.example.library.entity.Borrower;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookResponse(Long id, String isbn, String title, String author) {}


