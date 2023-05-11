package ru.practicum.shareit.exception;

public class ValidateException extends RuntimeException {
    public static final String OWNER_ID_NOT_BLANK = "[X-Sharer-User-Id parameter must not be null or blank]";
    public static final String ENDTIME_BEFORE_STARTTIME = "[End time must not be early then start time]";
    public static final String USER_NOT_RELATED_FOR_BOOKING = "Access denied. UserId not related for this booking";
    public static final String STATE_INCORRECT_INPUT = "Unknown state: ";
    public static final String STATUS_PARAM_NOT_BLANK = "Status must not be blank";
    public static final String STATUS_NOT_WAITING = "Status already changed from WAITING";
    public static final String ITEM_NOT_HAVE_BOOKING_BY_USER = "Item not have completed booking by this user";

    public ValidateException(String message) {
        super(message);
    }
}