package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.Item;

public interface ItemStorage {
    Item add(Item item);

    Item update(Long itemId, Item savedItem);

    Item get(Long itemId);
}