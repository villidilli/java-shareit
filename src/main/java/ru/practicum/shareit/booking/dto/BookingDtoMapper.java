package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingDtoMapper {
    public static BookingLongDto toBookingDto(Booking booking) {
        BookingLongDto bookingDto = new BookingLongDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(BookingStatus.valueOf(booking.getStatus().name()));
        bookingDto.setBooker(booking.getBooker());
        bookingDto.setItem(booking.getItem());
        return bookingDto;
    }

    public static Booking toBooking(BookingIncomeDto bookingIncomeDto, Long bookerId) {
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
