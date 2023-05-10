package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
@Slf4j
public class BookingDtoMapper {

    public static BookingResponseDto toBookingDto(Booking booking) {
        log.debug("ПРИШЕЛ БУКИНГ В TO DTO ---------- " + booking);
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setStart(booking.getStart());
        bookingResponseDto.setEnd(booking.getEnd());
        bookingResponseDto.setStatus(booking.getStatus());
        bookingResponseDto.setBooker(new BookingResponseDto.UserShortDto(booking.getBooker().getId()));
        bookingResponseDto.setItem(new BookingResponseDto.ItemShortDto(booking.getItem().getId(),
                                                                        booking.getItem().getName()));
        return bookingResponseDto;
    }

//    public static Booking toBooking(BookingRequestDto bookingIncomeDto, Item item, User booker) {
//        Booking booking = new Booking();
//        booking.setStart(bookingIncomeDto.getStart());
//        booking.setEnd(bookingIncomeDto.getEnd());
//        booking.setItem(item);
//        booking.setBooker(booker);
//        booking.setStatus(WAITING);
//        return booking;
//    }

    public static Booking toBooking(BookingRequestDto bookingIncomeDto, User booker, Item item) {
        Booking booking = new Booking();
        booking.setStart(bookingIncomeDto.getStart());
        booking.setEnd(bookingIncomeDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(WAITING);
        return booking;
    }
}
