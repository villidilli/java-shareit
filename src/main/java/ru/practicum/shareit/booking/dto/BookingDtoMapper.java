package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class BookingDtoMapper {

    public static BookingResponseDto toBookingDto(Booking booking) {
        log.debug("/toBookingDto");
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setStart(booking.getStart());
        bookingResponseDto.setEnd(booking.getEnd());
        bookingResponseDto.setStatus(booking.getStatus());
        bookingResponseDto.setBooker(new BookingResponseDto.UserShortDto(booking.getBooker().getId()));
        bookingResponseDto.setItem(
                new BookingResponseDto.ItemShortDto(booking.getItem().getId(), booking.getItem().getName()));
        return bookingResponseDto;
    }

    public static Booking toBooking(BookingRequestDto bookingIncomeDto, User booker, Item item, BookingStatus status) {
        log.debug("/toBooking");
        Booking booking = new Booking();
        booking.setStart(bookingIncomeDto.getStart());
        booking.setEnd(bookingIncomeDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        return booking;
    }
}