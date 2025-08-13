package com.example.library.exception;

public class DuplicateIsbnException extends RuntimeException {
    public DuplicateIsbnException(String msg){ super(msg); }
}
