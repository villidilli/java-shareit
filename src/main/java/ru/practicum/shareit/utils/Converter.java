package ru.practicum.shareit.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

public class Converter {
    public static String bindingResultToString(BindingResult br) {
        StringBuilder sb = new StringBuilder();
        List<FieldError> errors = br.getFieldErrors();
        for (FieldError error : errors) {
            sb.append("[" + error.getField() + "] -> [");
            sb.append(error.getDefaultMessage() + "]");
        }
        return sb.toString();
    }
}