package com.example.library.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BorrowerResponse(Long id, String name, String email,String message,BookResponse bookResponse) { }


