package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.PARAM_USER_ID;
import static ru.practicum.shareit.request.controller.ItemRequestController.*;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class BookingController {
    public static final String PARAM_NAME_BOOKING_STATE = "state";
    public static final String DEFAULT_BOOKING_STATE = "ALL";
    public static final String PARAM_APPROVED = "approved";
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto create(@Valid @RequestBody BookingRequestDto bookingIncomeDto,
                                     BindingResult br,
                                     @RequestHeader(name = PARAM_USER_ID) Long bookerId) {
        log.debug("/create");
        return bookingService.create(bookingIncomeDto, br, bookerId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto update(@RequestHeader(name = PARAM_USER_ID) Long ownerId,
                                     @PathVariable Long bookingId,
                                     @RequestParam(name = PARAM_APPROVED) String status) {
        log.debug("/update");
        return bookingService.update(bookingId, ownerId, status);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto getByUser(@RequestHeader(name = PARAM_USER_ID) Long userId,
                                        @PathVariable Long bookingId) {
        log.debug("/getByUser");
        return bookingService.getByUser(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getAllByBooker(
                    @RequestHeader(name = PARAM_USER_ID) Long userId,
                    @RequestParam(name = PARAM_NAME_BOOKING_STATE, defaultValue = DEFAULT_BOOKING_STATE) String state,
                    @RequestParam(value = FIRST_PAGE, defaultValue = DEFAULT_FIRST_PAGE) @PositiveOrZero Integer from,
                    @RequestParam(value = SIZE_VIEW, defaultValue = DEFAULT_SIZE_VIEW) @Positive Integer size) {
        log.debug("/getAllByBooker");
        return bookingService.getAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getAllByOwner(
                    @RequestHeader(name = PARAM_USER_ID) Long userId,
                    @RequestParam(name = PARAM_NAME_BOOKING_STATE, defaultValue = DEFAULT_BOOKING_STATE) String state,
                    @RequestParam(value = FIRST_PAGE, defaultValue = DEFAULT_FIRST_PAGE) @PositiveOrZero Integer from,
                    @RequestParam(value = SIZE_VIEW, defaultValue = DEFAULT_SIZE_VIEW) @Positive Integer size) {
        log.debug("/getAllByOwner");
        return bookingService.getAllByOwner(userId, state, from, size);
    }
}