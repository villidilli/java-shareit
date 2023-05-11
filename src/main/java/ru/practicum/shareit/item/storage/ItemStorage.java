package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long ownerId);

    List<Item> findByNameContainsIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                                                                        String textNameSearch, String textDescrSearch);

    Item findByIdAndAvailableIsTrue(Long itemId);
}