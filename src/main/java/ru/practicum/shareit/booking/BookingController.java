package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingIncomeDto;
import ru.practicum.shareit.booking.dto.BookingLongDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    public static final String PARAM_NAME_BOOKED_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingLongDto create(@Valid @RequestBody BookingIncomeDto bookingIncomeDto,
                                 BindingResult br,
                                 @RequestHeader(name = PARAM_NAME_BOOKED_ID) Long bookerId) {
        log.debug("/create");
        return bookingService.create(bookingIncomeDto, br, bookerId);
    }
}
