package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.Constant.*;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto create(@RequestBody BookingRequestDto bookingIncomeDto,
                                     @RequestHeader(PARAM_USER_ID) Long bookerId) {
        log.debug("/create");
        return bookingService.create(bookingIncomeDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto update(@RequestHeader(PARAM_USER_ID) Long ownerId,
                                     @PathVariable Long bookingId,
                                     @RequestParam(PARAM_APPROVED) String status) {
        log.debug("/update");
        return bookingService.update(bookingId, ownerId, status);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto getByUser(@RequestHeader(PARAM_USER_ID) Long userId,
                                        @PathVariable Long bookingId) {
        log.debug("/getByUser");
        return bookingService.getByUser(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getAllByBooker(@RequestHeader(PARAM_USER_ID) Long userId,
                                                   @RequestParam(PARAM_NAME_BOOKING_STATE) String state,
                                                   @RequestParam(FIRST_PAGE) Integer from,
                                                   @RequestParam(SIZE_VIEW) Integer size) {
        log.debug("/getAllByBooker");
        return bookingService.getAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getAllByOwner(@RequestHeader(PARAM_USER_ID) Long userId,
                                                  @RequestParam(PARAM_NAME_BOOKING_STATE) String state,
                                                  @RequestParam(FIRST_PAGE) Integer from,
                                                  @RequestParam(SIZE_VIEW) Integer size) {
        log.debug("/getAllByOwner");
        return bookingService.getAllByOwner(userId, state, from, size);
    }
}