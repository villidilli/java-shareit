package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
@Slf4j
public class BookingDtoMapper {
//
//    private static ItemStorage itemStorage;
//
//    private static UserStorage userStorage;
//
//    @Autowired
//    private BookingDtoMapper(ItemStorage itemStorage, UserStorage userStorage) {
//        BookingDtoMapper.itemStorage = itemStorage;
//        BookingDtoMapper.userStorage = userStorage;
//    }

    public static BookingResponseDto toBookingDto(Booking booking) {
        log.debug("ПРИШЕЛ БУКИНГ В TO DTO ---------- " + booking);
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setStart(booking.getStart());
        bookingResponseDto.setEnd(booking.getEnd());
        bookingResponseDto.setBooker(booking.getBooker());
        bookingResponseDto.setItem(booking.getItem());
        bookingResponseDto.setStatus(booking.getStatus());
        log.debug("СЕТЫЫЫЫЫЫЫЫЫЫ ПРОШЛИИИИИИИИИИИИИИИИИИИ");
        log.debug(" ДТО С ОШИБКОЙ ========== " + bookingResponseDto);
        return bookingResponseDto;
    }

    public static Booking toBooking(BookingRequestDto bookingIncomeDto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(bookingIncomeDto.getStart());
        booking.setEnd(bookingIncomeDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(WAITING);
        log.debug(" ПОЛУЧИЛСЯ ТАКОЙ  БУКИНГ ____ " + booking);
        return booking;
    }
}
