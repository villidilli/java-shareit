package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@Setter
@Getter
public class ExceptionResponse {
    protected String errorClass;
    protected String error;
    protected Timestamp timestamp;

    public ExceptionResponse(Exception exception) {
        this.error = exception.getMessage();
        this.errorClass = exception.getClass().getSimpleName();
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }
}