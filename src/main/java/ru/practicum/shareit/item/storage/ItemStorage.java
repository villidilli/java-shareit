package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;

import java.util.List;

import static ru.practicum.shareit.exception.NotFoundException.ITEM_NOT_FOUND;

public interface ItemStorage {

    Item add(Item item);

    Item update(Long itemId, Item savedItem);

    Item get(Long itemId);

    List<Item> getAll();

    void isExist(Long itemId);
}