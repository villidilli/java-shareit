package ru.practicum.shareit.exception;

public class ValidateException extends RuntimeException {
    public static final String DUPLICATE_EMAIL = "[Email must be unique]";

    public ValidateException(String message) {
        super(message);
    }
}