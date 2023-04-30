package ru.practicum.shareit.booking;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Booking {
    @EqualsAndHashCode.Include
    @Positive
    private Long id;
    @NotBlank
    private LocalDateTime start;
    @NotBlank
    private LocalDateTime end;
    @NotNull
    private Item item;
    @NotNull
    private User booker;
    private BookingStatus bookingStatus;
}