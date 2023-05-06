package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class BookingResponseDto {
    private Long id;
    @NotBlank(message = "Item ID must not be null or empty")
    private LocalDateTime start;
    @NotBlank(message = "End time must not be null or empty")
    private LocalDateTime end;
    private BookingStatus status;
    private User booker;
    private Item item;
}