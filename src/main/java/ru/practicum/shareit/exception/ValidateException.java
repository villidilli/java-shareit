package ru.practicum.shareit.exception;

public class ValidateException extends RuntimeException {
    public static final String DUPLICATE_EMAIL = "[Email не уникален]";

    public ValidateException(String message) {
        super(message);
    }
}