package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.practicum.shareit.exception.NotFoundException.ITEM_NOT_FOUND;

@Repository
@Slf4j
public class ItemStorageInMemory implements ItemStorage {
    private static Long countId = 1L;
    private final Map<Long, Item> items = new HashMap<>();

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
        log.debug("/get");
        return items.get(itemId);
    }

    @Override
    public List<Item> getAll() {
        log.debug("/getAll");
        return new ArrayList<>(items.values());
    }

    @Override
    public void isExist(Long itemId) {
        log.debug("/isExist");
        if (items.get(itemId) == null) throw new NotFoundException(ITEM_NOT_FOUND);
    }
}