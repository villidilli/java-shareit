package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ItemDtoMapper {
    public static ItemDtoWithBooking toItemDtoWithBooking(Item item, Booking bookingLast, Booking bookingNext) {
        log.debug("/toItemDtoWithBooking");
        ItemDtoWithBooking itemDtoBook = new ItemDtoWithBooking();
        itemDtoBook.setId(item.getId());
        itemDtoBook.setName(item.getName());
        itemDtoBook.setDescription(item.getDescription());
        itemDtoBook.setAvailable(item.getAvailable());
        if (bookingLast != null) itemDtoBook.setLastBooking(
                    new ItemDtoWithBooking.BookingShortDto(bookingLast.getId(), bookingLast.getBooker().getId()));
        if (bookingNext != null) itemDtoBook.setNextBooking(
                    new ItemDtoWithBooking.BookingShortDto(bookingNext.getId(), bookingNext.getBooker().getId()));
        return itemDtoBook;
    }

    public static ItemDto toItemDto(Item item) {
        log.debug("/toItemDto");
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        if (item.getRequest() != null) itemDto.setRequestId(item.getRequest().getId());
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, User owner, ItemRequest request) {
        log.debug("/toItem");
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        item.setRequest(request);
        return item;
    }
}