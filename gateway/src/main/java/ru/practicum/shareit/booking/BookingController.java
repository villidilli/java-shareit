package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.Constant.*;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient client;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Valid @RequestBody BookingRequestDto bookingIncomeDto,
                                 BindingResult br,
                                 @RequestHeader(name = PARAM_USER_ID) Long bookerId) {
        log.debug("[GATEWAY]/create");
        return client.create(bookerId, bookingIncomeDto, br);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> update(@RequestHeader(name = PARAM_USER_ID) Long ownerId,
                                         @PathVariable Long bookingId,
                                         @RequestParam(name = PARAM_APPROVED) String status) {
        log.debug("/[GATEWAY]/update");
        return client.update(ownerId, bookingId, status);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getByUser(@RequestHeader(name = PARAM_USER_ID) Long userId,
                                        @PathVariable Long bookingId) {
        log.debug("[GATEWAY]/getByUser");
        return client.getByUser(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllByBooker(
                    @RequestHeader(name = PARAM_USER_ID) Long userId,
                    @RequestParam(name = PARAM_NAME_BOOKING_STATE, defaultValue = DEFAULT_BOOKING_STATE) String state,
                    @RequestParam(value = FIRST_PAGE, defaultValue = DEFAULT_FIRST_PAGE) @PositiveOrZero Integer from,
                    @RequestParam(value = SIZE_VIEW, defaultValue = DEFAULT_SIZE_VIEW) @Positive Integer size) {
        log.debug("[GATEWAY]/getAllByBooker");
        return client.getAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllByOwner(
                    @RequestHeader(name = PARAM_USER_ID) Long userId,
                    @RequestParam(name = PARAM_NAME_BOOKING_STATE, defaultValue = DEFAULT_BOOKING_STATE) String state,
                    @RequestParam(value = FIRST_PAGE, defaultValue = DEFAULT_FIRST_PAGE) @PositiveOrZero Integer from,
                    @RequestParam(value = SIZE_VIEW, defaultValue = DEFAULT_SIZE_VIEW) @Positive Integer size) {
        log.debug("[GATEWAY]/getAllByOwner");
        return client.getAllByOwner(userId, state, from, size);
    }
}