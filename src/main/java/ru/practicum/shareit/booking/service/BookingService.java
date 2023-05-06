package ru.practicum.shareit.booking.service;

import org.springframework.validation.BindingResult;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

public interface BookingService {
    BookingResponseDto create(BookingRequestDto bookingIncomeDto, BindingResult br, Long bookerId);
}
