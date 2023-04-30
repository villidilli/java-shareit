package ru.practicum.shareit.exception;

public class ValidateException extends RuntimeException {
    public static final String DUPLICATE_EMAIL = "[Email must be unique]";
    public static final String EMAIL_NOT_BLANK = "[Email must not be null or blank]";
    public static final String OWNER_ID_NOT_BLANK = "[X-Sharer-User-Id parameter must not be null or blank]";

    public ValidateException(String message) {
        super(message);
    }
}