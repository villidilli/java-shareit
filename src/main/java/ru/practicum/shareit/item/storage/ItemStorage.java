package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {
    Page<Item> findByOwnerId(Long ownerId, Pageable page);

    Page<Item> findByNameContainsIgnoreCaseOrDescriptionContainingIgnoreCase(
                                                        String textNameSearch, String textDescrSearch, Pageable page);

    Item findByIdAndAvailableIsTrue(Long itemId);

    List<Item> findByRequest_IdIn(List<Long> requestIds);
}