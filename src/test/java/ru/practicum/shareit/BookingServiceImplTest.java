package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto.ItemShortDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto.UserShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    BookingStorage mockBookingStorage;
    @Mock
    UserService mockUserService;
    @Mock
    ItemService mockItemService;
    @Mock
    UserStorage mockUserStorage;
    @Mock
    ItemStorage mockItemStorage;
    @Mock
    BindingResult br;
    @Mock
    BookingService mockBookingService;

    BookingService bookingService = new BookingServiceImpl(mockBookingStorage,
            mockUserService,
            mockItemService,
            mockUserStorage,
            mockItemStorage);

    BookingRequestDto bookingReqDto1;
    User user1;
    Item item1;
    Booking booking1;
    ItemRequest request1;
    BookingResponseDto bookingRespDto;
    ItemShortDto itemShortDto;
    UserShortDto userShortDto;
    LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0,0);
    LocalDateTime end = LocalDateTime.of(2023, 1, 2, 0, 0);

    @BeforeEach
    public void beforeEach() {


        itemShortDto = new ItemShortDto(1L, "itemShortDto name");
        userShortDto = new UserShortDto(1L);

        user1 = new User();
        user1.setId(1L);
        user1.setName("user name");
        user1.setEmail("user@user.ru");

        request1 = new ItemRequest();
        request1.setRequester(user1);
        request1.setCreated(LocalDateTime.of(2023, 5,17,15,15));
        request1.setDescription("request description");

        item1 = new Item();
        item1.setId(1L);
        item1.setName("item name");
        item1.setDescription("item description");
        item1.setAvailable(true);
        item1.setOwner(user1);
        item1.setRequest(request1);

//        bookingRespDto = new BookingResponseDto();
//        bookingRespDto.setId(1L);
//        bookingRespDto.setStatus(BookingStatus.WAITING);
//        bookingRespDto.setItem(itemShortDto);
//        bookingRespDto.setBooker(userShortDto);
//        bookingRespDto.setStart(start);
//        bookingRespDto.setEnd(end);

        bookingReqDto1 = new BookingRequestDto();
        bookingReqDto1.setItemId(1L);
        bookingReqDto1.setStart(start);
        bookingReqDto1.setEnd(end);

        booking1 = BookingDtoMapper.toBooking(bookingReqDto1, user1, item1, BookingStatus.WAITING);
        booking1.setId(1L);
    }

    @Test
    public void t() {
        Mockito.doNothing().when(mockBookingService).isBookerIsOwner(anyLong(), anyLong());
        Mockito.doNothing().when(mockUserService).isExist(anyLong());
        Mockito.doNothing().when(mockItemService).isExist(anyLong());
        Mockito.doNothing().when(mockItemService).isItemAvailable(anyLong());

        Mockito
                .when(mockBookingStorage.save(Mockito.any(Booking.class)))
                .thenReturn(booking1);

        BookingResponseDto actualDto = bookingService.create(bookingReqDto1, br, 1L);

        Assertions.assertEquals(actualDto.getId(), 1L);


    }

}
