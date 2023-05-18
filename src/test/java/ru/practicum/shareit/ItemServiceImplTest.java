package ru.practicum.shareit;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

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

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

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
public class ItemServiceImplTest {
    @Mock
    ItemStorage mockItemStorage;
    @Mock
    UserService mockUserService;
    @Mock
    ObjectMapper mockObjectMapper;
    @Mock
    BookingStorage mockBookingStorage;
    @Mock
    CommentStorage mockCommentStorage;
    @Mock
    UserStorage mockUserStorage;
    @Mock
    ItemRequestService mockRequestService;
    @Mock
    ItemRequestStorage mockRequestStorage;
    @Mock
    ItemService mockItemService;
    ItemService itemService;
    User owner1;
    User author1;
    ItemDto itemDto1;
    ItemDto itemDtoUpdate;
    Comment comment;
    CommentDto commentDto;
    LocalDateTime date1;
    LocalDateTime date2;
    LocalDateTime dateNow;
    Item item1;
    BindingResult br;
    Map<String, String> map;
    Booking booking1;

    @BeforeEach
    public void beforeEach() {
        itemService = new ItemServiceImpl(mockItemStorage,
                                        mockUserService,
                                        mockObjectMapper,
                                        mockBookingStorage,
                                        mockCommentStorage,
                                        mockUserStorage,
                                        mockRequestService,
                                        mockRequestStorage);
        date1 = LocalDateTime.of(2023, 5, 17, 0, 0);
        date2 = LocalDateTime.of(2023, 5, 17, 6, 0);
        dateNow = LocalDateTime.of(2023, 5, 18, 0, 0);
        owner1 = new User(1L, "user1 name", "user1@user.ru");
        author1 = new User(2L, "author1 name", "author1@author.ru");
        item1 = new Item(1L, owner1, "item1 name", "item1 desc", true, null);
        comment = new Comment(1L, "text", item1, author1, date1);
        commentDto = new CommentDto(1L, "text", author1.getName(), date1);
        itemDto1 = new ItemDto(1L,
                "itemDto1",
                "itemDto1 desc",
                true,
                List.of(commentDto),
                null);
        itemDtoUpdate = new ItemDto();
        itemDtoUpdate.setName("new name");
        br = new BindException(item1, null);
        booking1 = new Booking(1L, date1, date2, item1, author1, WAITING);
    }

    @Test
    public void createItem() {
        when(mockUserStorage.getReferenceById(anyLong())).thenReturn(owner1);
        when(mockItemStorage.save(any(Item.class))).thenReturn(item1);
        ItemDto actualDto = itemService.create(itemDto1, br, 1L);
        assertNotNull(actualDto);
        assertEquals(actualDto.getId(), item1.getId());
        assertEquals(actualDto.getName(), item1.getName());
        verify(mockItemStorage, times(1)).save(any(Item.class));
    }

    @Test
    public void updateItem() {
        ObjectMapper objectMapper = new ObjectMapper();
        when(mockUserStorage.getReferenceById(anyLong())).thenReturn(owner1);
        when(mockItemStorage.findById(anyLong())).thenReturn(Optional.of(item1));
        Map<String, String> mappp = objectMapper.convertValue(item1, Map.class);
        when(mockObjectMapper.convertValue(any(Item.class), eq(Map.class))).thenReturn(mappp);
        when(mockObjectMapper.convertValue(any(Map.class), eq(Item.class))).thenReturn(item1);
        when(mockItemStorage.save(any(Item.class))).thenReturn(item1);
        when(mockItemStorage.existsById(anyLong())).thenReturn(true);
        ItemDto actualDto = itemService.update(item1.getId(), itemDtoUpdate, owner1.getId());
        assertNotNull(actualDto);
        assertEquals(actualDto.getId(), item1.getId());
        verify(mockItemStorage, times(1)).save(any(Item.class));
    }

    @Test
    public void getByItemId() {
        when(mockItemStorage.findById(anyLong())).thenReturn(Optional.of(item1));
        when(mockItemStorage.existsById(anyLong())).thenReturn(true);
        when(mockBookingStorage.findByItem_Owner_IdAndItem_Id(anyLong(), anyLong())).thenReturn(List.of(booking1));
        ItemDtoWithBooking actualDto = itemService.get(item1.getId(), owner1.getId());
        assertNotNull(actualDto);
        assertEquals(item1.getId(), actualDto.getId());
        assertNotNull(actualDto.getLastBooking());
        assertEquals(booking1.getId(), actualDto.getLastBooking().getId());
        assertNotNull(actualDto.getComments());
        assertEquals(0, actualDto.getComments().size());
    }

    @Test
    public void getByOwner() {
        Page<Item> page = new PageImpl<>(List.of(item1));
        when(mockItemStorage.findByOwnerId(owner1.getId(), PageRequest.of(0, 999))).thenReturn(page);
        when(mockBookingStorage.findByItem_Owner_Id(anyLong())).thenReturn(List.of(booking1));
        List<ItemDtoWithBooking> actualDto = itemService.getByOwner(owner1.getId(),
                                                                    Integer.parseInt(DEFAULT_FIRST_PAGE),
                                                                    Integer.parseInt(DEFAULT_SIZE_VIEW));
        assertNotNull(actualDto);
        assertEquals(1, actualDto.size());
        assertEquals(item1.getId(), actualDto.get(0).getId());
        assertNotNull(actualDto.get(0).getLastBooking());
        assertEquals(booking1.getId(), actualDto.get(0).getLastBooking().getId());
        assertNotNull(actualDto.get(0).getComments());
    }

    @Test
    public void search() {
        Page<Item> page = new PageImpl<>(List.of(item1));
        when(mockItemStorage.findByNameContainsIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                "item", "item", PageRequest.of(0, 999)))
                .thenReturn(page);
        List<ItemDto> actualDto = itemService.search("item",
                                                     Integer.parseInt(DEFAULT_FIRST_PAGE),
                                                     Integer.parseInt(DEFAULT_SIZE_VIEW));
        assertNotNull(actualDto);
        assertEquals(1, actualDto.size());
        assertEquals(item1.getId(), actualDto.get(0).getId());
        assertEquals(item1.getName(), actualDto.get(0).getName());
    }

    @Test
    public void createComment() {
        when(mockBookingStorage.countBookingsByBooker_IdAndItem_IdAndEndBefore(author1.getId(), item1.getId(), any())).thenReturn(1L);
        when(mockItemStorage.existsById(item1.getId())).thenReturn(true);
        when(mockItemStorage.findById(item1.getId())).thenReturn(Optional.of(item1));
        CommentDto actualDto = itemService.createComment(commentDto, item1.getId(), author1.getId(), br);
        assertNotNull(actualDto);
    }
}
