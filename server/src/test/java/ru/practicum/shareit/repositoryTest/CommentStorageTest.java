package ru.practicum.shareit.repositoryTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@Transactional
@DataJpaTest(properties = "spring.datasource.url = jdbc:h2:mem:test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentStorageTest {
    @Autowired
    private CommentStorage commentStorage;
    @Autowired
    private BookingStorage bookingStorage;
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemStorage itemStorage;

    private User owner1;
    private User savedOwner1;
    private User owner2;
    private User savedOwner2;
    private User booker1;
    private User savedBooker1;
    private User booker2;
    private User savedBooker2;
    private Item item1;
    private Item savedItem1;
    private Item item2;
    private Item savedItem2;
    private Booking booking1;
    private Booking savedBooking1;
    private Booking booking2;
    private Booking savedBooking2;
    private Booking booking3;
    private Booking savedBooking3;
    private Booking booking4;
    private Booking savedBooking4;
    private Booking booking5;
    private Booking savedBooking5;
    private Comment comment1;
    private Comment savedComment1;
    private Comment comment2;
    private Comment savedComment2;
    private LocalDateTime startOld;
    private LocalDateTime endOld;
    private LocalDateTime startCur;
    private LocalDateTime endCur;
    private LocalDateTime startFut;
    private LocalDateTime endFut;
    private LocalDateTime now;
    private LocalDateTime anyStart;
    private LocalDateTime anyEnd;

    @BeforeEach
    public void beforeEach() {
        startOld = LocalDateTime.of(2022,1,1,0,0);
        endOld = LocalDateTime.of(2022,6,1,0,0);
        startCur = LocalDateTime.of(2022,12,1,0,0);
        endCur = LocalDateTime.of(2023,12,1,0,0);
        startFut = LocalDateTime.of(2024,1,1,0,0);
        endFut = LocalDateTime.of(2025,1,1,0,0);
        now = LocalDateTime.of(2023,5,24,0,0);
        anyStart = LocalDateTime.of(2022,12,31,0,0);
        anyEnd = LocalDateTime.of(2021, 12,12, 0,0);

        owner1 = new User(null, "owner1", "owner1@email.ru");
        owner2 = new User(null, "owner2", "owner2@email.ru");
        savedOwner1 = userStorage.save(owner1);
        savedOwner2 = userStorage.save(owner2);

        booker1 = new User(null, "booker1", "booker1@email.ru");
        booker2 = new User(null, "booker2", "booker2@email.ru");
        savedBooker1 = userStorage.save(booker1);
        savedBooker2 = userStorage.save(booker2);

        item1 = new Item(null, owner1, "item1", "desc", true, null);
        item2 = new Item(null, owner2, "item2", "desc", true, null);
        savedItem1 = itemStorage.save(item1);
        savedItem2 = itemStorage.save(item2);

        booking1 = new Booking(null, startOld, endOld, savedItem1, savedBooker1, WAITING);
        booking2 = new Booking(null, startCur, endCur, savedItem1, savedBooker1, BookingStatus.APPROVED);
        booking3 = new Booking(null, startFut, endFut, savedItem1, savedBooker1, BookingStatus.REJECTED);
        booking4 = new Booking(null, startOld, endOld, savedItem2, savedBooker2, WAITING);
        booking5 = new Booking(null, startFut, endFut, savedItem2, savedBooker2, BookingStatus.APPROVED);
        savedBooking1 = bookingStorage.save(booking1);
        savedBooking2 = bookingStorage.save(booking2);
        savedBooking3 = bookingStorage.save(booking3);
        savedBooking4 = bookingStorage.save(booking4);
        savedBooking5 = bookingStorage.save(booking5);

        comment1 = new Comment(null, "comment1", savedItem1, savedBooker1, LocalDateTime.now());
        comment2 = new Comment(null, "comment2", savedItem2, savedBooker2, LocalDateTime.now());
        savedComment1 = commentStorage.save(comment1);
        savedComment2 = commentStorage.save(comment2);
    }

    @Test
    public void findByItem_IdIn() {
        List<Long> idsForSearch = List.of(savedItem2.getId(), savedItem1.getId());

        List<Comment> actual = commentStorage.findByItem_IdIn(idsForSearch);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(2, actual.size());
        Assertions.assertEquals(savedComment1.getId(), actual.get(0).getId());
        Assertions.assertEquals(savedComment2.getId(), actual.get(1).getId());
    }

    @AfterEach
    public void afterEach() {
        userStorage.deleteAll();
        commentStorage.deleteAll();
        itemStorage.deleteAll();
        bookingStorage.deleteAll();
    }
}