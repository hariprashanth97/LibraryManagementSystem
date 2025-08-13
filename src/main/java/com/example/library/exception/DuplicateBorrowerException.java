package com.example.library.exception;

public class DuplicateBorrowerException extends RuntimeException {
    public DuplicateBorrowerException(String msg){ super(msg); }
}
