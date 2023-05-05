package ru.practicum.shareit.booking.service;

import org.springframework.validation.BindingResult;
import ru.practicum.shareit.booking.dto.BookingIncomeDto;
import ru.practicum.shareit.booking.dto.BookingLongDto;

public interface BookingService {
    BookingLongDto create(BookingIncomeDto bookingIncomeDto, BindingResult br, Long bookerId);
}
