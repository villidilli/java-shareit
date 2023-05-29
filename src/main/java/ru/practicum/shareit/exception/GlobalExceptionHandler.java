package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static ru.practicum.shareit.exception.ValidateException.*;

@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class GlobalExceptionHandler {

    public static String bindingResultToString(BindingResult br) {
        StringBuilder sb = new StringBuilder();
        List<FieldError> errors = br.getFieldErrors();
        for (FieldError error : errors) {
            sb.append("[" + error.getField() + "] -> [");
            sb.append(error.getDefaultMessage() + "]");
        }
        return sb.toString();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse exceptionHandler(ValidateException e) {
        log.debug("/ValidateExceptionHandler");
        logException(HttpStatus.BAD_REQUEST, e);
        return new ExceptionResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse exceptionHandler(NotFoundException e) {
        log.debug("/NotFoundExceptionHandler");
        logException(HttpStatus.NOT_FOUND, e);
        return new ExceptionResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse exceptionHandler(DataIntegrityViolationException e) {
        log.debug("/DataIntegrityViolationHandler");
        logException(HttpStatus.CONFLICT, e);
        return new ExceptionResponse(new FieldConflictException(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse exceptionHandler(MissingRequestHeaderException e) {
        log.debug("/MissingRequestHeaderExceptionHandler");
        logException(HttpStatus.BAD_REQUEST, e);
        return new ExceptionResponse(new ValidateException(OWNER_ID_NOT_BLANK));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse exceptionHandler(MissingServletRequestParameterException e) {
        log.debug("/MissingRequestHeaderExceptionHandler");
        logException(HttpStatus.BAD_REQUEST, e);
        return new ExceptionResponse(new ValidateException(STATUS_PARAM_NOT_BLANK));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse exceptionHandler(ConstraintViolationException e) {
        log.debug("/IllegalArgumentExceptionHandler");
        logException(HttpStatus.BAD_REQUEST, e);
        return new ExceptionResponse(new ValidateException(ILLEGAL_ARGUMENT));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse exceptionHandler(Throwable e) {
        log.debug("/OtherExceptionHandler");
        logException(HttpStatus.INTERNAL_SERVER_ERROR, e);
        return new ExceptionResponse(e);
    }

    private void logException(HttpStatus status, Throwable exception) {
        log.debug("[" + exception.getClass().getSimpleName() + "] [" + status.value() + "]" + exception.getMessage());
    }
}