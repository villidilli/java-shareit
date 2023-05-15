package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class BookingRequestDto {
    @NotNull(message = "Item ID must not be null")
    private Long itemId;
    @FutureOrPresent
    @NotNull(message = "Start time must not be null")
    private LocalDateTime start;
    @FutureOrPresent
    @NotNull(message = "End time must not be null")
    private LocalDateTime end;
}