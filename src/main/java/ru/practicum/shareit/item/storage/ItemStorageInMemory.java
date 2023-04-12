package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class ItemStorageInMemory implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
//    private final Map<Long, Long> itemOwner = new HashMap<>();
    private static Long countId = 1L;
    @Override
    public Item add(Item item) {
        log.debug("/add");
        item.setId(countId);
        items.put(countId, item);
//        itemOwner.put(item.getId(), item.getOwner());
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

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }
}