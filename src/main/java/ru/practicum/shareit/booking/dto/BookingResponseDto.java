package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;

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
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private UserShortDto booker;
    private ItemShortDto item;

    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class UserShortDto {
        private Long id;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class ItemShortDto {
        private Long id;
        private String name;
    }
}