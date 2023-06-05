package ru.practicum.shareit.repositoryTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@DataJpaTest(properties = "spring.datasource.url = jdbc:h2:mem:test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemStorageTest {
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private ItemRequestStorage requestStorage;

    private User owner1;
    private User savedOwner1;
    private User owner2;
    private User savedOwner2;
    private Item item1;
    private Item savedItem1;
    private Item item2;
    private Item savedItem2;
    private Item item3;
    private Item savedItem3;
    private User requester1;
    private User requester2;
    private User savedRequester1;
    private User savedRequester2;
    private ItemRequest request1;
    private ItemRequest request2;
    private ItemRequest savedRequest1;
    private ItemRequest savedRequest2;
    private Pageable page;

    @BeforeEach
    public void beforeEach() {
        owner1 = new User(null, "owner1", "owner1@email.ru");
        owner2 = new User(null, "owner2", "owner2@email.ru");
        savedOwner1 = userStorage.save(owner1);
        savedOwner2 = userStorage.save(owner2);

        requester1 = new User(null, "requester1", "requester1@email.ru");
        requester2 = new User(null, "requester2", "requester2@email.ru");
        savedRequester1 = userStorage.save(requester1);
        savedRequester2 = userStorage.save(requester2);

        request1 = new ItemRequest(null, "desc", savedRequester1, LocalDateTime.now());
        request2 = new ItemRequest(null, "desc", savedRequester1, LocalDateTime.now());
        savedRequest1 = requestStorage.save(request1);
        savedRequest2 = requestStorage.save(request2);

        item1 = new Item(null, savedOwner1, "i", "desc", true, savedRequest1);
        item2 = new Item(null, savedOwner1, "it", "item", false, null);
        item3 = new Item(null, savedOwner2, "item", "desc", true, savedRequest2);
        savedItem1 = itemStorage.save(item1);
        savedItem2 = itemStorage.save(item2);
        savedItem3 = itemStorage.save(item3);
    }

    @Test
    public void findByOwnerId() {
        page = PageRequest.of(1, 1);

        Page<Item> actualPage = itemStorage.findByOwnerId(savedOwner1.getId(), page);

        assertNotNull(actualPage);
        List<Item> actualList = actualPage.toList();
        assertEquals(1, actualList.size());
        assertEquals(savedItem2.getId(), actualList.get(0).getId());
    }

    @Test
    public void findByNameContainsIgnoreCaseOrDescriptionContainingIgnoreCase() {
        page = PageRequest.of(0, 5);

        Page<Item> actualPage =
                itemStorage.findByNameContainsIgnoreCaseOrDescriptionContainingIgnoreCase(
                                                                    "item", "item", page);

        assertNotNull(actualPage);
        List<Item> actualList = actualPage.toList();
        assertEquals(2, actualList.size());
        assertEquals(savedItem2.getId(), actualList.get(0).getId());
        assertEquals(savedItem3.getId(), actualList.get(1).getId());
    }

    @Test
    public void findByIdAndAvailableIsTrue() {
        Item actual = itemStorage.findByIdAndAvailableIsTrue(savedItem1.getId());

        assertNotNull(actual);
        assertEquals(savedItem1.getId(), actual.getId());
    }

    @Test
    public void findByRequest_IdIn() {
        List<Long> requestsIds = List.of(savedRequest1.getId(), savedRequest2.getId());

        List<Item> actual = itemStorage.findByRequest_IdIn(requestsIds);

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals(savedItem1.getId(), actual.get(0).getId());
        assertEquals(savedItem3.getId(), actual.get(1).getId());
    }

    @AfterEach
    public void afterEach() {
        userStorage.deleteAll();
        itemStorage.deleteAll();
        requestStorage.deleteAll();
    }
}