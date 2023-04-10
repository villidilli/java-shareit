package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse exceptionHandler(ValidateException e) {
        log.debug("/validate exception handler");
        logException(HttpStatus.BAD_REQUEST, e);
        return new ExceptionResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse exceptionHandler(FieldConflictException e) {
        log.debug("/validate exception handler");
        logException(HttpStatus.CONFLICT, e);
        return new ExceptionResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse exceptionHandler(Exception e) {
        log.debug("/other exception handler");
        logException(HttpStatus.INTERNAL_SERVER_ERROR, e);
        return new ExceptionResponse(e);
    }

    private void logException(HttpStatus status, Exception exception) {
        log.debug("[" + exception.getClass().getSimpleName() + "] [" + status.value() + "]" + exception.getMessage());
    }

}
