package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;

@NoArgsConstructor
@Setter
@Getter
public class ExceptionResponse {
    protected String exceptionClass;
    protected String exceptionMessage;
    protected Timestamp timestamp;

    public ExceptionResponse(Exception exception) {
        this.exceptionClass = exception.getClass().getSimpleName();
        this.exceptionMessage = exception.getMessage();
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }
}