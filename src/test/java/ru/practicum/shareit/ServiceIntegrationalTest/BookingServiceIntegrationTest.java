package ru.practicum.shareit.ServiceIntegrationalTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.model.BookingState.*;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;

@Transactional
@SpringBootTest(properties = "spring.datasource.url = jdbc:h2:mem:test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {
    @Autowired
    UserService userService;
    @Autowired
    BookingStorage bookingStorage;
    @Autowired
    ItemService itemService;
    @Autowired
    BookingService bookingService;

    @Test
    public void getAllByBooker() {
        UserDto owner1 = new UserDto(null, "owner1", "owner@email.ru");
        UserDto user1 = new UserDto(null, "user1", "user1@email.ru");
        UserDto savedOwner1 = userService.create(owner1, new BindException(owner1, null));
        UserDto savedUser1 = userService.create(user1, new BindException(user1, null));

        ItemDto item1 = new ItemDto(null, "item1", "desc", true, null, null);
        ItemDto savedItem1 = itemService.create(item1, new BindException(item1, null), savedOwner1.getId());

        LocalDateTime startOld = LocalDateTime.of(2022, 1, 1, 0, 0);
        LocalDateTime endOld = LocalDateTime.of(2022, 6, 6, 0, 0);
        LocalDateTime startCur = LocalDateTime.of(2022, 12, 12, 0, 0);
        LocalDateTime endCur = LocalDateTime.of(2023, 12, 12, 0, 0);
        LocalDateTime startFut = LocalDateTime.of(2024, 12, 12, 0, 0);
        LocalDateTime endFut = LocalDateTime.of(2025, 12, 12, 0, 0);

        BookingRequestDto booking1 = new BookingRequestDto(savedItem1.getId(), startOld, endOld);
        BookingRequestDto booking2 = new BookingRequestDto(savedItem1.getId(), startCur, endCur);
        BookingRequestDto booking3 = new BookingRequestDto(savedItem1.getId(), startFut, endFut);
        BookingResponseDto savedBooking1 = bookingService.create(booking1, new BindException(booking1, null), savedUser1.getId());
        BookingResponseDto savedBooking2 = bookingService.create(booking2, new BindException(booking2, null), savedUser1.getId());
        BookingResponseDto savedBooking3 = bookingService.create(booking3, new BindException(booking3, null), savedUser1.getId());

        bookingService.update(savedBooking2.getId(), savedOwner1.getId(), String.valueOf(true));
        bookingService.update(savedBooking3.getId(), savedOwner1.getId(), String.valueOf(false));

        List<BookingResponseDto> actualListAll = bookingService.getAllByBooker(savedUser1.getId(), ALL.name(), 0, 2);
        assertEquals(2, actualListAll.size());
        BookingResponseDto actualBooking1 = actualListAll.get(0);
        BookingResponseDto actualBooking2 = actualListAll.get(1);
        assertEquals(savedBooking3.getId(), actualBooking1.getId());
        assertEquals(savedBooking2.getId(), actualBooking2.getId());
        assertEquals(REJECTED, actualBooking1.getStatus());
        assertEquals(APPROVED, actualBooking2.getStatus());

        List<BookingResponseDto> actualListCurrent = bookingService.getAllByBooker(savedUser1.getId(), CURRENT.name(), 0, 2);
        assertEquals(1, actualListCurrent.size());
        assertEquals(savedBooking2.getId(), actualListCurrent.get(0).getId());
        assertEquals(APPROVED, actualListCurrent.get(0).getStatus());

        List<BookingResponseDto> actualListPast = bookingService.getAllByBooker(savedUser1.getId(), PAST.name(), 0, 2);
        assertEquals(1, actualListPast.size());
        assertEquals(savedBooking1.getId(), actualListPast.get(0).getId());
        assertEquals(BookingStatus.WAITING, actualListPast.get(0).getStatus());

        List<BookingResponseDto> actualListFuture = bookingService.getAllByBooker(savedUser1.getId(), FUTURE.name(), 0, 2);
        assertEquals(1, actualListFuture.size());
        assertEquals(savedBooking3.getId(), actualListFuture.get(0).getId());
        assertEquals(REJECTED, actualListFuture.get(0).getStatus());

        List<BookingResponseDto> actualListWaiting = bookingService.getAllByBooker(savedUser1.getId(), WAITING.name(),0, 2);
        assertEquals(1, actualListWaiting.size());
        assertEquals(savedBooking1.getId(), actualListWaiting.get(0).getId());
        assertEquals(BookingStatus.WAITING, actualListWaiting.get(0).getStatus());

        List<BookingResponseDto> actualListRejected = bookingService.getAllByBooker(savedUser1.getId(), REJECTED.name(),0, 2);
        assertEquals(1, actualListRejected.size());
        assertEquals(savedBooking3.getId(), actualListRejected.get(0).getId());
        assertEquals(REJECTED, actualListRejected.get(0).getStatus());
    }
}