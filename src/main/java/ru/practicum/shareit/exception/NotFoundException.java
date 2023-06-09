package ru.practicum.shareit.exception;

public class NotFoundException extends RuntimeException {
    public static final String USER_NOT_FOUND = "[User not found]";
    public static final String ITEM_NOT_FOUND = "[Item not found]";
    public static final String BOOKING_NOT_FOUND = "[Booking not found]";
    public static final String OWNER_NOT_MATCH_ITEM = "[Owner does not match item]";
    public static final String BOOKER_IS_OWNER_ITEM = "[Booker is owner by item]";
    public static final String REQUEST_NOT_FOUND = "[Request not found]";

    public NotFoundException(String message) {
        super(message);
    }
}