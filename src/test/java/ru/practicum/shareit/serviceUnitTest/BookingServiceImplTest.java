package ru.practicum.shareit.serviceUnitTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;
import org.springframework.validation.BindingResult;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingStorage;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemStorage;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import static ru.practicum.shareit.booking.controller.BookingController.DEFAULT_BOOKING_STATE;
import static ru.practicum.shareit.booking.model.BookingState.*;
import static ru.practicum.shareit.booking.model.BookingStatus.*;
import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;
import static ru.practicum.shareit.exception.NotFoundException.*;
import static ru.practicum.shareit.request.controller.ItemRequestController.DEFAULT_FIRST_PAGE;
import static ru.practicum.shareit.request.controller.ItemRequestController.DEFAULT_SIZE_VIEW;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingStorage mockBookingStorage;
    @Mock
    private UserService mockUserService;
    @Mock
    private ItemService mockItemService;
    @Mock
    private UserStorage mockUserStorage;
    @Mock
    private ItemStorage mockItemStorage;
    @Mock
    private BindingResult br;

    private BookingService bookingService;

    private BookingRequestDto bookingReqDto1;
    private User booker1;
    private User owner1;
    private Item item1;
    private Booking booking1;

    LocalDateTime date1 = LocalDateTime.of(2023, 5, 10, 0,0);
    LocalDateTime date2 = LocalDateTime.of(2023, 5, 20, 0, 0);

    @BeforeEach
    public void beforeEach() {
         bookingService = new BookingServiceImpl(mockBookingStorage,
                mockUserService,
                mockItemService,
                mockUserStorage,
                mockItemStorage);
        owner1 = new User(1L, "owner1", "owner1@owner.ru");
        booker1 = new User(2L, "booker1", "booker1@booker.ru");;
        item1 = new Item(1L, owner1, "item1", "item1 description", true, null);
        bookingReqDto1 = new BookingRequestDto(1L, date1, date2);
        booking1 = new Booking(1L, date1, date2, item1, booker1, WAITING);
    }

    @Test
    public void createBooking() {
        when(mockItemStorage.getReferenceById(anyLong())).thenReturn(item1);
        when(mockBookingStorage.save(any(Booking.class))).thenReturn(booking1);

        BookingResponseDto actualDto = bookingService.create(bookingReqDto1, br, 2L);

        assertEquals(actualDto.getId(), 1L);
        assertEquals(actualDto.getItem().getId(), 1L);
        assertEquals(actualDto.getBooker().getId(), 2L);
        assertEquals(actualDto.getStatus(), WAITING);
        assertEquals(actualDto.getStart(), date1);
        assertEquals(actualDto.getEnd(), date2);
        verify(mockBookingStorage, times(1)).save(any(Booking.class));
    }

    @Test
    public void createBookingBookerNotFound() {
        doThrow(new NotFoundException(USER_NOT_FOUND)).when(mockUserService).isExist(anyLong());

        NotFoundException actualException =
                assertThrows(NotFoundException.class, () -> bookingService.create(bookingReqDto1, br, 1L));

        assertEquals(actualException.getMessage(), USER_NOT_FOUND);
    }

    @Test
    public void createBookingItemNotFound() {
        doThrow(new NotFoundException(ITEM_NOT_FOUND)).when(mockItemService).isExist(anyLong());

        NotFoundException actualException =
                assertThrows(NotFoundException.class, () -> bookingService.create(bookingReqDto1, br, 1L));

        assertEquals(actualException.getMessage(), ITEM_NOT_FOUND);
    }

    @Test
    public void createBookingBookerEqualsOwner() {
        item1.getOwner().setId(1L);
        when(mockItemStorage.getReferenceById(anyLong())).thenReturn(item1);

        NotFoundException actualException =
                assertThrows(NotFoundException.class, () -> bookingService.create(bookingReqDto1, br, 1L));

        assertEquals(actualException.getMessage(), BOOKER_IS_OWNER_ITEM);
    }

    @Test
    public void createBookingItemNotAvailable() {
        doThrow(new ValidateException(ITEM_NOT_FOUND)).when(mockItemService).isItemAvailable(anyLong());

        ValidateException actualException =
                assertThrows(ValidateException.class, () -> bookingService.create(bookingReqDto1, br, 2L));

        assertEquals(actualException.getMessage(), ITEM_NOT_FOUND);
    }

    @Test
    public void updateBookingWhenStatusApproved() {
        when(mockBookingStorage.existsById(anyLong())).thenReturn(true);
        when(mockBookingStorage.getReferenceById(anyLong())).thenReturn(booking1);
        when(mockBookingStorage.save(any(Booking.class))).thenReturn(booking1);

        BookingResponseDto actualDto = bookingService.update(1L, 1L, "true");

        assertEquals(actualDto.getId(), 1L);
        assertEquals(actualDto.getStatus(), APPROVED);
        verify(mockBookingStorage, times(1)).save(any(Booking.class));
    }

    @Test
    public void updateBookingWhenStatusRejected() {
        when(mockBookingStorage.existsById(anyLong())).thenReturn(true);
        when(mockBookingStorage.getReferenceById(anyLong())).thenReturn(booking1);
        when(mockBookingStorage.save(any(Booking.class))).thenReturn(booking1);

        BookingResponseDto actualDto = bookingService.update(1L, 1L, "false");

        assertEquals(actualDto.getId(), 1L);
        assertEquals(actualDto.getStatus(), REJECTED);
        verify(mockBookingStorage, times(1)).save(any(Booking.class));
    }

    @Test
    public void getAllByBookerStateAllReturnPageEmpty() {
        Page<Booking> pageFromDb = Page.empty();
        when(mockBookingStorage.findAllByBooker_Id(anyLong(), any(Pageable.class))).thenReturn(pageFromDb);

        List<BookingResponseDto> actualDtos = bookingService.getAllByBooker(2L,
                DEFAULT_BOOKING_STATE,
                Integer.parseInt(DEFAULT_FIRST_PAGE),
                Integer.parseInt(DEFAULT_SIZE_VIEW));

        assertEquals(actualDtos.size(), pageFromDb.getTotalElements());
        verify(mockBookingStorage, times(1)).findAllByBooker_Id(anyLong(), any(Pageable.class));
    }

    @Test
    public void getAllByBookerStateCurrent() {
        Page<Booking> pageFromDb = new PageImpl<>(List.of(booking1));
        when(mockBookingStorage.findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(anyLong(),
                                                                                any(LocalDateTime.class),
                                                                                any(LocalDateTime.class),
                                                                                any(Pageable.class)))
                .thenReturn(pageFromDb);

        List<BookingResponseDto> actyalDtos =
                bookingService.getAllByBooker(booker1.getId(), String.valueOf(CURRENT), 0, 5);

        assertEquals(actyalDtos.size(), pageFromDb.toList().size());
        assertEquals(actyalDtos.get(0).getId(), pageFromDb.toList().get(0).getId());
        verify(mockBookingStorage, times(1)).findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Pageable.class));
    }

    @Test
    public void getAllByBookerStatePast() {
        Page<Booking> pageFromDb = new PageImpl<>(List.of(booking1));
        when(mockBookingStorage.findAllByBooker_idAndEndIsBefore(anyLong(),
                                                                any(LocalDateTime.class),
                                                                any(Pageable.class)))
                .thenReturn(pageFromDb);

        List<BookingResponseDto> actualDtos =
                bookingService.getAllByBooker(booker1.getId(), String.valueOf(PAST), 0, 5);

        assertEquals(actualDtos.size(), pageFromDb.toList().size());
        assertEquals(actualDtos.get(0).getId(), pageFromDb.toList().get(0).getId());
        verify(mockBookingStorage, times(1)).findAllByBooker_idAndEndIsBefore(
                                                                                            anyLong(),
                                                                                            any(LocalDateTime.class),
                                                                                            any(Pageable.class));
    }

    @Test
    public void getAllByBookerStateFuture() {
        Page<Booking> pageFromDb = new PageImpl<>(List.of(booking1));
        when(mockBookingStorage.findAllByBooker_idAndStartIsAfter(anyLong(),
                                                                any(LocalDateTime.class),
                                                                any(Pageable.class)))
                .thenReturn(pageFromDb);

        List<BookingResponseDto> actyalDtos =
                bookingService.getAllByBooker(booker1.getId(), String.valueOf(FUTURE), 0, 5);

        assertEquals(actyalDtos.size(), pageFromDb.toList().size());
        assertEquals(actyalDtos.get(0).getId(), pageFromDb.toList().get(0).getId());
        verify(mockBookingStorage, times(1)).findAllByBooker_idAndStartIsAfter(
                                                                                            anyLong(),
                                                                                            any(LocalDateTime.class),
                                                                                            any(Pageable.class));
    }

    @Test
    public void getAllByBookerStateWaiting() {
        Page<Booking> pageFromDb = new PageImpl<>(List.of(booking1));
        when(mockBookingStorage.findAllByBooker_IdAndStatusIs(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(pageFromDb);

        List<BookingResponseDto> actyalDtos =
                bookingService.getAllByBooker(booker1.getId(), String.valueOf(WAITING), 0, 5);

        assertEquals(actyalDtos.size(), pageFromDb.toList().size());
        assertEquals(actyalDtos.get(0).getId(), pageFromDb.toList().get(0).getId());
        verify(mockBookingStorage, times(1)).findAllByBooker_IdAndStatusIs(
                                                                                            anyLong(),
                                                                                            any(BookingStatus.class),
                                                                                            any(Pageable.class));
    }

    @Test
    public void getAllByBookerStateRejected() {
        Page<Booking> pageFromDb = new PageImpl<>(List.of(booking1));
        when(mockBookingStorage.findAllByBooker_IdAndStatusIs(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(pageFromDb);

        List<BookingResponseDto> actyalDtos =
                bookingService.getAllByBooker(booker1.getId(), String.valueOf(REJECTED), 0, 5);

        assertEquals(actyalDtos.size(), pageFromDb.toList().size());
        assertEquals(actyalDtos.get(0).getId(), pageFromDb.toList().get(0).getId());
        verify(mockBookingStorage, times(1)).findAllByBooker_IdAndStatusIs(
                anyLong(),
                any(BookingStatus.class),
                any(Pageable.class));
    }

    @Test
    public void getAllByOwnerStateAllReturnPageEmpty() {
        Page<Booking> pageFromDb = Page.empty();
        when(mockBookingStorage.findAllByItem_Owner_Id(anyLong(), any(Pageable.class)))
                .thenReturn(pageFromDb);

        List<BookingResponseDto> actualDtos = bookingService.getAllByOwner(2L,
                                                                                DEFAULT_BOOKING_STATE,
                                                                                Integer.parseInt(DEFAULT_FIRST_PAGE),
                                                                                Integer.parseInt(DEFAULT_SIZE_VIEW));

        assertEquals(actualDtos.size(), pageFromDb.getTotalElements());
        verify(mockBookingStorage, times(1))
                .findAllByItem_Owner_Id(anyLong(), any(Pageable.class));
    }

    @Test
    public void getAllByOwnerStateCurrent() {
        Page<Booking> pageFromDb = new PageImpl<>(List.of(booking1));
        when(mockBookingStorage.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(anyLong(),
                                                                                    any(LocalDateTime.class),
                                                                                    any(LocalDateTime.class),
                                                                                    any(Pageable.class)))
                .thenReturn(pageFromDb);

        List<BookingResponseDto> actualDtos =
                bookingService.getAllByOwner(booker1.getId(), String.valueOf(CURRENT), 0, 5);

        assertEquals(actualDtos.size(), pageFromDb.toList().size());
        assertEquals(actualDtos.get(0).getId(), pageFromDb.toList().get(0).getId());
        verify(mockBookingStorage, times(1))
                .findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(anyLong(),
                                                                    any(LocalDateTime.class),
                                                                    any(LocalDateTime.class),
                                                                    any(Pageable.class));
    }

    @Test
    public void getAllByOwnerStatePast() {
        Page<Booking> pageFromDb = new PageImpl<>(List.of(booking1));
        when(mockBookingStorage.findAllByItem_Owner_IdAndEndIsBefore(anyLong(),
                                                                    any(LocalDateTime.class),
                                                                    any(Pageable.class)))
                .thenReturn(pageFromDb);

        List<BookingResponseDto> actyalDtos =
                bookingService.getAllByOwner(booker1.getId(), String.valueOf(PAST), 0, 5);

        assertEquals(actyalDtos.size(), pageFromDb.toList().size());
        assertEquals(actyalDtos.get(0).getId(), pageFromDb.toList().get(0).getId());
        verify(mockBookingStorage, times(1)).findAllByItem_Owner_IdAndEndIsBefore(
                                                                                            anyLong(),
                                                                                            any(LocalDateTime.class),
                                                                                            any(Pageable.class));
    }

    @Test
    public void getAllByOwnerStateFuture() {
        Page<Booking> pageFromDb = new PageImpl<>(List.of(booking1));
        when(mockBookingStorage.findAllByItem_Owner_IdAndStartIsAfter(anyLong(),
                                                                    any(LocalDateTime.class),
                                                                    any(Pageable.class)))
                .thenReturn(pageFromDb);

        List<BookingResponseDto> actyalDtos =
                bookingService.getAllByOwner(booker1.getId(), String.valueOf(FUTURE), 0, 5);

        assertEquals(actyalDtos.size(), pageFromDb.toList().size());
        assertEquals(actyalDtos.get(0).getId(), pageFromDb.toList().get(0).getId());
        verify(mockBookingStorage, times(1)).findAllByItem_Owner_IdAndStartIsAfter(
                                                                                            anyLong(),
                                                                                            any(LocalDateTime.class),
                                                                                            any(Pageable.class));
    }

    @Test
    public void getAllByOwnerStateWaiting() {
        Page<Booking> pageFromDb = new PageImpl<>(List.of(booking1));
        when(mockBookingStorage.findAllByItem_Owner_IdAndStatusIs(anyLong(),
                                                                any(BookingStatus.class),
                                                                any(Pageable.class)))
                .thenReturn(pageFromDb);

        List<BookingResponseDto> actyalDtos =
                bookingService.getAllByOwner(booker1.getId(), String.valueOf(WAITING), 0, 5);

        assertEquals(actyalDtos.size(), pageFromDb.toList().size());
        assertEquals(actyalDtos.get(0).getId(), pageFromDb.toList().get(0).getId());
        verify(mockBookingStorage, times(1)).findAllByItem_Owner_IdAndStatusIs(
                                                                                            anyLong(),
                                                                                            any(BookingStatus.class),
                                                                                            any(Pageable.class));
    }

    @Test
    public void getAllByOwnerStateRejected() {
        Page<Booking> pageFromDb = new PageImpl<>(List.of(booking1));
        when(mockBookingStorage.findAllByItem_Owner_IdAndStatusIs(anyLong(),
                                                                any(BookingStatus.class),
                                                                any(Pageable.class)))
                .thenReturn(pageFromDb);

        List<BookingResponseDto> actyalDtos =
                bookingService.getAllByOwner(booker1.getId(), String.valueOf(REJECTED), 0, 5);

        assertEquals(actyalDtos.size(), pageFromDb.toList().size());
        assertEquals(actyalDtos.get(0).getId(), pageFromDb.toList().get(0).getId());
        verify(mockBookingStorage, times(1)).findAllByItem_Owner_IdAndStatusIs(
                                                                                            anyLong(),
                                                                                            any(BookingStatus.class),
                                                                                            any(Pageable.class));
    }
}