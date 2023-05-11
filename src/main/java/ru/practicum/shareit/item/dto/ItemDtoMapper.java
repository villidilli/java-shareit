package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
@Component
public class ItemDtoMapper {
    private static UserStorage userStorage;

    @Autowired
    private ItemDtoMapper(UserStorage userStorage) {
        ItemDtoMapper.userStorage = userStorage;
    }

    public static ItemDtoWithBooking toItemDtoWithBooking(
            Item item, Booking bookingLast, Booking bookingNext) {
        log.debug("/toItemDtoWithBooking");
        ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking();
        itemDtoWithBooking.setId(item.getId());
        itemDtoWithBooking.setName(item.getName());
        itemDtoWithBooking.setDescription(item.getDescription());
        itemDtoWithBooking.setAvailable(item.getAvailable());
        if(bookingLast != null) {
            itemDtoWithBooking.setLastBooking(new ItemDtoWithBooking.BookingShortDto(bookingLast.getId(),
                    bookingLast.getBooker().getId()));
        }
        if(bookingNext != null) {
            itemDtoWithBooking.setNextBooking(new ItemDtoWithBooking.BookingShortDto(bookingNext.getId(),
                    bookingNext.getBooker().getId()));
        }
        return itemDtoWithBooking;
    }

    public static ItemDto toItemDto(Item item) {
        log.debug("/toItemDto");
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, Long ownerId) {
        log.debug("/toItem");
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(userStorage.getReferenceById(ownerId));
        return item;
    }
}