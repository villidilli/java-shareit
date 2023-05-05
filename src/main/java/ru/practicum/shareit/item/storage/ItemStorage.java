package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long ownerId);

    List<Item> findByAvailableIsTrueAndNameContainsIgnoreCaseOrDescriptionContainsIgnoreCase(String textToNameSearch,
                                                                                            String textToDescripSearch);

    List<Item> findByNameContainsIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String textNameSearch,
                                                                                               String textDescrSearch);

//    Item add(Item item);
//
//    Item update(Long itemId, Item savedItem);
//
//    Item get(Long itemId);
//
//    List<Item> getAll();
//
//    void isExist(Long itemId);
//
//    List<Item> getByOwner(Long userId);
}