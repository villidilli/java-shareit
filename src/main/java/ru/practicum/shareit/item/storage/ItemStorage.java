package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long ownerId);


    List<Item> findByNameContainsIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String textNameSearch,
                                                                                               String textDescrSearch);

//    Optional<Item> findByIdAndAvailableIsTrue(Long itemId);

    Item findByIdAndAvailableIsTrue(Long itemId);
}