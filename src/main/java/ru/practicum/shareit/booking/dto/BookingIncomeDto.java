package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
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
public class BookingIncomeDto {
    @NotNull(message = "Item ID must not be null")
    private Long itemId;
    @FutureOrPresent
    @NotNull(message = "Start time must not be null")
    private LocalDateTime start;
    @FutureOrPresent
    @NotNull(message = "End time must not be null")
    private LocalDateTime end;
}