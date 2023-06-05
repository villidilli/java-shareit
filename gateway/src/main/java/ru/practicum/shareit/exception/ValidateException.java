package ru.practicum.shareit.exception;

public class ValidateException extends RuntimeException {
    public static final String OWNER_ID_NOT_BLANK = "[X-Sharer-User-Id parameter must not be null or blank]";
    public static final String ENDTIME_BEFORE_STARTTIME = "[End time must not be early then start time]";
    public static final String STATE_INCORRECT_INPUT = "Unknown state: ";
    public static final String STATUS_PARAM_NOT_BLANK = "Status must not be blank";
    public static final String ILLEGAL_ARGUMENT = "Parameter entered incorrectly";

    public ValidateException(String message) {
        super(message);
    }
}