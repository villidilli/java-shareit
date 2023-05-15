package ru.practicum.shareit.booking.service;

import org.springframework.validation.BindingResult;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto create(BookingRequestDto bookingIncomeDto, BindingResult br, Long bookerId);

    BookingResponseDto update(Long bookingId, Long ownerId, String status);

    void isExist(Long bookingId);

    BookingResponseDto getByUser(Long userId, Long bookingId);

    List<BookingResponseDto> getAllByBooker(Long bookerId, String state);

    List<BookingResponseDto> getAllByOwner(Long userId, String state);
}