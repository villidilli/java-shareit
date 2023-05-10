package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.model.UserRole;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    public static final String PARAM_NAME_BOOKED_OWNER_ID = "X-Sharer-User-Id";
    public static final String PARAM_NAME_BOOKING_STATE = "state";
    public static final String DEFAULT_BOOKING_STATE = "ALL";
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto create(@Valid @RequestBody BookingRequestDto bookingIncomeDto,
                                     BindingResult br,
                                     @RequestHeader(name = PARAM_NAME_BOOKED_OWNER_ID) Long bookerId) {
        log.debug("/create");
        return bookingService.create(bookingIncomeDto, br, bookerId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto update(@RequestHeader(name = PARAM_NAME_BOOKED_OWNER_ID) Long ownerId,
                                     @PathVariable Long bookingId,
                                     @RequestParam(name = "approved") String status) {
        log.debug("/update");
        return bookingService.update(bookingId, ownerId, status);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto getByUser(@RequestHeader(name = PARAM_NAME_BOOKED_OWNER_ID) Long userId,
                                        @PathVariable Long bookingId) {
        log.debug("/getByUser");
        return bookingService.getByUser(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getAllByBooker(@RequestHeader(name = PARAM_NAME_BOOKED_OWNER_ID) Long userId,
                                                   @RequestParam(name = PARAM_NAME_BOOKING_STATE,
                                                                 required = false,
                                                                 defaultValue = DEFAULT_BOOKING_STATE) String state) {
        log.debug("/getAllByBooker");
        return bookingService.getAllByUser(userId, state, UserRole.BOOKER);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getAllByOwner(@RequestHeader(name = PARAM_NAME_BOOKED_OWNER_ID) Long userId,
                                                  @RequestParam(name = PARAM_NAME_BOOKING_STATE,
                                                          required = false,
                                                          defaultValue = DEFAULT_BOOKING_STATE) String state) {
        log.debug("/getAllByOwner");
        return bookingService.getAllByUser(userId, state, UserRole.OWNER);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> TESTgetAllByBooker() {
        return bookingService.TESTgetAll();
    }
}
