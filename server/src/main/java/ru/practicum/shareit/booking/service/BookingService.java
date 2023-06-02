package ru.practicum.shareit.booking.service;

import org.springframework.validation.BindingResult;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto create(BookingRequestDto bookingIncomeDto, Long bookerId);

    BookingResponseDto update(Long bookingId, Long ownerId, String status);

    void isExist(Long bookingId);

    void isBookerIsOwner(Long itemId, Long bookerId);

    BookingResponseDto getByUser(Long userId, Long bookingId);

    List<BookingResponseDto> getAllByBooker(Long bookerId, String state, Integer from, Integer size);

    List<BookingResponseDto> getAllByOwner(Long userId, String state, Integer from, Integer size);
}