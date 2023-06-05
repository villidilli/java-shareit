package ru.practicum.shareit.serviceIntegrationalTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = "spring.datasource.url = jdbc:h2:mem:test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemRequestService requestService;

    @Test
    public void getByOwner_thenReturnPageItems() {
        UserDto owner1 = new UserDto(null, "owner1", "owner@email.ru");
        UserDto user1 = new UserDto(null, "user1", "user1@email.ru");
        UserDto user2 = new UserDto(null, "user2", "user2@email.ru");

        UserDto savedOwner1 = userService.create(owner1);
        UserDto savedUser1 = userService.create(user1);

        ItemRequestDto request = new ItemRequestDto(null, "desc", LocalDateTime.now());
        ItemRequestDto savedRequest = requestService.create(request, savedUser1.getId());

        ItemDto item1 =
                new ItemDto(null, "item1", "desc", true, null, null);
        ItemDto item2 =
                new ItemDto(null, "item2", "desc", false, null, null);
        ItemDto item3 =
                new ItemDto(null, "item3", "desc", true, null, savedRequest.getId());
        ItemDto item4 =
                new ItemDto(null, "item4", "desc", true, null, null);
        ItemDto item5 =
                new ItemDto(null, "item5", "desc", true, null, null);

        ItemDto savedItem1 = itemService.create(item1, savedOwner1.getId());
        ItemDto savedItem2 = itemService.create(item2, savedOwner1.getId());
        ItemDto savedItem3 = itemService.create(item3, savedOwner1.getId());
        ItemDto savedItem4 = itemService.create(item4, savedOwner1.getId());
        ItemDto savedItem5 = itemService.create(item5, savedOwner1.getId());

        BookingRequestDto booking1 = new BookingRequestDto(
                savedItem3.getId(), LocalDateTime.now().minusHours(12), LocalDateTime.now().minusHours(1));
        BookingRequestDto booking2 = new BookingRequestDto(
                savedItem3.getId(), LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusHours(1));

        BookingResponseDto savedBooking1 = bookingService.create(
                booking1, savedUser1.getId());
        BookingResponseDto savedBooking2 = bookingService.create(
                booking2, savedUser1.getId());

        CommentDto comment = new CommentDto(null, "text", savedUser1.getName(), LocalDateTime.now());
        CommentDto savedComment = itemService.createComment(
                comment, savedItem3.getId(), savedUser1.getId());

        List<ItemDtoWithBooking> actual = itemService.getByOwner(savedOwner1.getId(), 2, 1);

        assertEquals(1, actual.size());
        ItemDtoWithBooking actualItem = actual.get(0);
        assertNotNull(savedItem3.getId());
        assertEquals(savedItem3.getId(), actualItem.getId());
        assertTrue(actualItem.getAvailable());
        assertNotNull(savedBooking1.getId());
        assertNotNull(savedBooking2.getId());
        assertEquals(savedBooking1.getId(), actualItem.getLastBooking().getId());
        assertEquals(savedBooking2.getId(), actualItem.getNextBooking().getId());
        assertNotNull(actualItem.getComments());
        assertNotNull(savedComment.getId());
        assertEquals(1, actualItem.getComments().size());
        assertEquals(savedComment.getId(), actualItem.getComments().get(0).getId());
    }
}