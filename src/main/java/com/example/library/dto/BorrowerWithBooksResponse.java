package com.example.library.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BorrowerWithBooksResponse(Long id, String name, String email, List<BookResponse> books) { }

