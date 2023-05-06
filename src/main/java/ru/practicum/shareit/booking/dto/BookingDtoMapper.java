package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class BookingDtoMapper {
    public static BookingResponseDto toBookingDto(Booking booking) {
        BookingResponseDto bookingLongDto = new BookingResponseDto();
        log.debug("МАППЕР в ДТО " + booking);
        bookingLongDto.setId(booking.getId());
        bookingLongDto.setStart(booking.getStart());
        bookingLongDto.setEnd(booking.getEnd());
        bookingLongDto.setBooker(booking.getBooker());
        bookingLongDto.setStatus(booking.getStatus());
        return bookingLongDto;
    }

    public static Booking toBooking(BookingRequestDto bookingIncomeDto, Long bookerId) {
        Booking booking = new Booking();
        booking.setStart(bookingIncomeDto.getStart());
        booking.setEnd(bookingIncomeDto.getEnd());
        Item item = new Item();
        item.setId(bookingIncomeDto.getItemId());
        booking.setItem(item);
        User user = new User();
        user.setId(bookerId);
        booking.setBooker(user);
        booking.setStatus(WAITING);
        return booking;
    }
}
