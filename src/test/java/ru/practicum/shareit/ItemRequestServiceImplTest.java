package ru.practicum.shareit;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    UserService userService;
    @Mock
    UserStorage userStorage;
    @Mock
    ItemStorage itemStorage;
    @Mock
    ItemRequestStorage requestStorage;
    @InjectMocks
    ItemRequestServiceImpl requestService;
}
