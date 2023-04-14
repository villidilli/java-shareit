package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.NotFoundException.ITEM_NOT_FOUND;

@Repository
@Slf4j
public class ItemStorageInMemory implements ItemStorage {
    private static Long countId = 1L;
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, Set<Long>> ownersWithItems = new HashMap<>();

    @Override
    public Item add(Item item) {
        log.debug("/add");
        item.setId(countId);
        items.put(countId, item);
        addToOwnerItems(item.getOwner(), item.getId());
        countId++;
        return item;
    }

    @Override
    public Item update(Long itemId, Item item) {
        log.debug("/update");
        items.put(itemId, item);
        addToOwnerItems(item.getOwner(), item.getId());
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

    public List<Item> getByOwner(Long ownerId) {
        log.debug("/getByOwner");
        Set<Long> ownerItems = ownersWithItems.get(ownerId);
        return ownerItems.stream().map(items::get).collect(Collectors.toList());
    }

    private void addToOwnerItems(Long ownerId, Long itemId) {
        Set<Long> ownerItems = ownersWithItems.getOrDefault(ownerId, new HashSet<>());
        ownerItems.add(itemId);
        ownersWithItems.put(ownerId, ownerItems);
    }
}