package ru.practicum.shareit.item.service;

import org.springframework.validation.BindingResult;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long ownerId, BindingResult br);

    ItemDto update(Long itemId, ItemDto item, Long ownerID);

    ItemDto get(Long itemId);

    List<ItemDto> getByOwner(Long ownerId);

    List<ItemDto> search(String text);

    void isExist(Long itemId);

    void checkAvailable(Long itemId);
}