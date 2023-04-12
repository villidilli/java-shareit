package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.HashMap;

@Repository
@Slf4j
public class ItemStorageInMemory implements ItemStorage {
    private final HashMap<Long, Item> items = new HashMap<>();
    private static Long countId = 1L;
    @Override
    public Item add(Item item) {
        log.debug("/add");
        item.setId(countId);
        items.put(countId, item);
        countId++;
        return item;
    }

    @Override
    public Item update(Long itemId, Item item) {
        log.debug("/update");
        items.put(itemId, item);
        return items.get(itemId);
    }

    @Override
    public Item get(Long itemId) {
        return items.get(itemId);
    }
}