package ru.practicum.shareit.exception;

public class NotFoundException extends RuntimeException {
    public static final String USER_NOT_FOUND = "[User not found]";
    public static final String ITEM_NOT_FOUND = "[Item not found]";

    public NotFoundException(String message) {
        super(message);
    }
}