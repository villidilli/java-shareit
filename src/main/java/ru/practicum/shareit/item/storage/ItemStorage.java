package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemStorage {
    Item add(Item item);

    Item update(Long itemId, Item savedItem);

    Item get(Long itemId);

    List<Item> getAll();
}