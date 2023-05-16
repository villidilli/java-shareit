package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    BookingStorage bookingStorage;
    @Mock
    UserService userService;
    @Mock
    ItemService itemService;
    @Mock
    UserStorage userStorage;
    @Mock
    ItemStorage itemStorage;

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void bookingCreateFromUser1ToItem1() {

    }

}
