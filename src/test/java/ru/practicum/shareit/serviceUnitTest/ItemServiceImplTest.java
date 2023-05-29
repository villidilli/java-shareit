package ru.practicum.shareit.serviceUnitTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.utils.PageConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;
import static ru.practicum.shareit.exception.NotFoundException.USER_NOT_FOUND;
import static ru.practicum.shareit.request.controller.ItemRequestController.DEFAULT_FIRST_PAGE;
import static ru.practicum.shareit.request.controller.ItemRequestController.DEFAULT_SIZE_VIEW;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserService userService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private CommentStorage commentStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private ItemRequestService requestService;
    @Mock
    private ItemRequestStorage requestStorage;
    @InjectMocks
    private ItemServiceImpl itemService;

    private User user1;
    private User user2;

    private ItemDto itemDto1;
    private ItemDto itemDtoUpdate;

    private Comment comment;
    private CommentDto commentDto;

    private LocalDateTime date1;
    private LocalDateTime date2;
    private LocalDateTime dateNow;

    private Item item1;

    private BindingResult br;

    private Booking booking1;

    @BeforeEach
    public void beforeEach() {
        itemService = new ItemServiceImpl(itemStorage,
                userService,
                objectMapper,
                bookingStorage,
                commentStorage,
                userStorage,
                requestService,
                requestStorage);

        date1 = LocalDateTime.of(2023, 5, 17, 0, 0);
        date2 = LocalDateTime.of(2023, 5, 17, 6, 0);
        dateNow = LocalDateTime.of(2023, 5, 18, 0, 0);

        user1 = new User(1L, "user1 name", "user1@user.ru");
        user2 = new User(2L, "author1 name", "author1@author.ru");

        item1 = new Item(1L, user1, "item1 name", "item1 desc", true, null);

        comment = new Comment(1L, "text", item1, user2, date1);
        commentDto = new CommentDto(1L, "text", user2.getName(), date1);

        itemDto1 = new ItemDto(1L,
                "itemDto1",
                "itemDto1 desc",
                true,
                List.of(commentDto),
                null);
        itemDtoUpdate = new ItemDto();
        itemDtoUpdate.setName("new name");

        br = new BindException(item1, null);

        booking1 = new Booking(1L, date1, date2, item1, user2, WAITING);
    }

    @Test
    public void createItem_ReturnSavedItem() {
        when(userStorage.getReferenceById(anyLong())).thenReturn(user1);
        when(itemStorage.save(any(Item.class))).thenReturn(item1);

        ItemDto actualDto = itemService.create(itemDto1, br, 1L);

        assertNotNull(actualDto);
        assertEquals(actualDto.getId(), item1.getId());
        assertEquals(actualDto.getName(), item1.getName());
        verify(itemStorage, times(1)).save(any(Item.class));
    }

    @Test
    public void createItem_whenUserNotFound_thenNotFoundExceptionThrow() {
        doThrow(new NotFoundException(USER_NOT_FOUND)).when(userService).isExist(anyLong());

        NotFoundException actual = assertThrows(NotFoundException.class,
                () -> itemService.create(itemDto1, br, 1L));

        assertNotNull(actual);
        assertTrue(actual.getMessage().contains("User not found"));
        verify(itemStorage, never()).save(any(Item.class));
    }

    @Test
    public void updateItem() {
        ObjectMapper objectMapper = new ObjectMapper();
        when(userStorage.getReferenceById(anyLong())).thenReturn(user1);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item1));
        Map<String, String> mappp = objectMapper.convertValue(item1, Map.class);
        when(this.objectMapper.convertValue(any(Item.class), eq(Map.class))).thenReturn(mappp);
        when(this.objectMapper.convertValue(any(Map.class), eq(Item.class))).thenReturn(item1);
        when(itemStorage.save(any(Item.class))).thenReturn(item1);
        when(itemStorage.existsById(anyLong())).thenReturn(true);

        ItemDto actualDto = itemService.update(item1.getId(), itemDtoUpdate, user1.getId());

        assertNotNull(actualDto);
        assertEquals(actualDto.getId(), item1.getId());
        verify(itemStorage, times(1)).save(any(Item.class));
    }

    @Test
    public void getByItemId() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item1));
        when(itemStorage.existsById(anyLong())).thenReturn(true);
        when(bookingStorage.findByItem_Owner_IdAndItem_Id(anyLong(), anyLong())).thenReturn(List.of(booking1));

        ItemDtoWithBooking actualDto = itemService.get(item1.getId(), user1.getId());

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

        when(itemStorage.findByOwnerId(anyLong(), any(PageConfig.class))).thenReturn(page);
        when(bookingStorage.findByItem_Owner_Id(anyLong())).thenReturn(List.of(booking1));

        List<ItemDtoWithBooking> actualDto = itemService.getByOwner(user1.getId(),
                                                                    0,
                                                                    999);
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

        when(itemStorage.findByNameContainsIgnoreCaseOrDescriptionContainingIgnoreCase(any(), any(), any()))
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
    public void createComment_thenReturnSavedComment() {
        when(itemStorage.existsById(item1.getId())).thenReturn(true);
        when(bookingStorage.countBookingsByBooker_IdAndItem_IdAndEndBefore(
                anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(1L);
        when(commentStorage.save(any(Comment.class))).thenReturn(comment);

        CommentDto actual = itemService.createComment(commentDto, item1.getId(), user2.getId(), br);

        assertNotNull(actual);
        assertEquals(commentDto.getId(), actual.getId());
        assertEquals(commentDto.getText(), actual.getText());
        verify(commentStorage, times(1)).save(any(Comment.class));
    }

    @Test
    public void createComment_whenItemNotFound_thenNotFoundExceptionThrow() {
        NotFoundException actual = assertThrows(NotFoundException.class,
                () -> itemService.createComment(commentDto, item1.getId(), user2.getId(), br));

        assertNotNull(actual);
        assertTrue(actual.getMessage().contains("Item not found"));
        verify(commentStorage, never()).save(any(Comment.class));
    }

    @Test
    public void createComment_whenUserNotCompletedBookingForItem_thenValidateExceptionThrow() {
        when(itemStorage.existsById(item1.getId())).thenReturn(true);
        when(bookingStorage.countBookingsByBooker_IdAndItem_IdAndEndBefore(
                anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(0L);

        ValidateException actual = assertThrows(ValidateException.class,
                () -> itemService.createComment(commentDto, item1.getId(), user2.getId(), br));

        assertNotNull(actual);
        assertTrue(actual.getMessage().contains("Item not have completed booking by this user"));
        verify(commentStorage, never()).save(any(Comment.class));
    }

    @Test
    public void isItemAvailable_whenItemNotAvailable_thenValidateExceptionThrow() {
        ValidateException actual = assertThrows(ValidateException.class,
                () -> itemService.isItemAvailable(item1.getId()));

        assertNotNull(actual);
        assertTrue(actual.getMessage().contains("Item not found"));
    }
}