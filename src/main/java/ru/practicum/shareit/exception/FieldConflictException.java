package ru.practicum.shareit.exception;

public class FieldConflictException extends RuntimeException {
    public static final String EMAIL_NOT_UNIQUE = "[Email not unique]";

    public FieldConflictException(String message) {
        super(message);
    }
}