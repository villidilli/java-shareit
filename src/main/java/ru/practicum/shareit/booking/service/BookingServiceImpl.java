package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingIncomeDto;
import ru.practicum.shareit.booking.dto.BookingLongDto;
import ru.practicum.shareit.booking.storage.BookingDbStorage;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import static ru.practicum.shareit.booking.dto.BookingDtoMapper.toBooking;
import static ru.practicum.shareit.booking.dto.BookingDtoMapper.toBookingDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingDbStorage bookingStorage;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingLongDto create(BookingIncomeDto bookingIncomeDto, BindingResult br, Long bookerId) {
        log.debug("/create");
        annotationValidate(br);
        userService.getByIdOrThrow(bookerId);
        itemService.getByIdOrThrow(bookingIncomeDto.getItemId());
        itemService.checkAvailable(bookingIncomeDto.getItemId());
        Booking booking = toBooking(bookingIncomeDto, bookerId);
        return toBookingDto(bookingStorage.save(booking));
    }

    private void annotationValidate(BindingResult br) {
        log.debug("/annotationValidate");
        if (br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
    }
}