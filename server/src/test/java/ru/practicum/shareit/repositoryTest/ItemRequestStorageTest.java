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
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

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
public class ItemRequestStorageTest {
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemRequestStorage requestStorage;

    private User requester1;
    private User requester2;
    private User savedRequester1;
    private User savedRequester2;

    private ItemRequest request1;
    private ItemRequest request2;
    private ItemRequest request3;
    private ItemRequest request4;

    private ItemRequest savedRequest1;
    private ItemRequest savedRequest2;
    private ItemRequest savedRequest3;
    private ItemRequest savedRequest4;

    private Pageable page;

    @BeforeEach
    public void beforeEach() {
        requester1 = new User(null, "requester1", "requester1@email.ru");
        requester2 = new User(null, "requester2", "requester2@email.ru");
        savedRequester1 = userStorage.save(requester1);
        savedRequester2 = userStorage.save(requester2);

        request1 = new ItemRequest(null, "desc", savedRequester1, LocalDateTime.now());
        request2 = new ItemRequest(null, "desc", savedRequester1, LocalDateTime.now());
        request3 = new ItemRequest(null, "desc", savedRequester1, LocalDateTime.now());
        request4 = new ItemRequest(null, "desc", savedRequester2, LocalDateTime.now());
        savedRequest1 = requestStorage.save(request1);
        savedRequest2 = requestStorage.save(request2);
        savedRequest3 = requestStorage.save(request3);
        savedRequest4 = requestStorage.save(request4);
    }

    @Test
    public void findByRequester_Id() {
        List<ItemRequest> actual = requestStorage.findByRequester_Id(savedRequester1.getId(), Sort.unsorted());

        assertNotNull(actual);
        assertEquals(3, actual.size());
        assertEquals(savedRequest1.getId(), actual.get(0).getId());
        assertEquals(savedRequest2.getId(), actual.get(1).getId());
        assertEquals(savedRequest3.getId(), actual.get(2).getId());
    }

    @Test
    public void findByRequester_IdNot() {
        page = PageRequest.of(2, 1);

        Page<ItemRequest> actual = requestStorage.findByRequester_IdNot(savedRequester2.getId(), page);

        assertNotNull(actual);
        assertEquals(1, actual.toList().size());
        assertEquals(savedRequest3.getId(), actual.toList().get(0).getId());
    }

    @Test
    public void findByIdIs() {
        ItemRequest actual = requestStorage.findByIdIs(savedRequest2.getId());

        assertNotNull(actual);
        assertEquals(savedRequest2.getId(), actual.getId());
        assertEquals(savedRequest2.getRequester().getId(), actual.getRequester().getId());
    }

    @AfterEach
    public void afterEach() {
        userStorage.deleteAll();
        requestStorage.deleteAll();
    }
}