package ru.practicum.shareit.item.service;

import org.springframework.validation.BindingResult;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long ownerId);

    ItemDto update(Long itemId, ItemDto item, Long ownerID);

    ItemDtoWithBooking get(Long itemId, Long ownerId);

    List<ItemDtoWithBooking> getByOwner(Long ownerId, Integer from, Integer size);

    List<ItemDto> search(String text, Integer from, Integer size);

    void isExist(Long itemId);

    void isItemAvailable(Long itemId);

    void isOwnerOfItem(Long itemId, Long ownerId);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long bookerId);
}