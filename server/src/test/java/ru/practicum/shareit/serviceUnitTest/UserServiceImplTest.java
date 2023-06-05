package ru.practicum.shareit.serviceUnitTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.exception.NotFoundException.USER_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserStorage userStorage;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private UserServiceImpl userService;
    @Captor
    private ArgumentCaptor<Long> idCaptor;

    private User user1;
    private BindingResult br;
    private UserDto userDto;

    @BeforeEach
    public void beforeEach() {
        user1 = new User(1L, "name", "user1@user.ru");
        br = new BindException(user1, null);
        userDto = new UserDto(1L, "name", "user1@user.ru");
    }

    @Test
    public void createUser_thenReturnSavedUser() {
        when(userStorage.save(any(User.class))).thenReturn(user1);

        UserDto actual = userService.create(userDto);

        assertNotNull(actual);
        assertEquals(user1.getId(), actual.getId());
        assertEquals(user1.getEmail(), actual.getEmail());
        verify(userStorage, times(1)).save(any(User.class));
    }

    @Test
    public void updateUser_whenUserFound_thenReturnUpdatedUser() {
        ObjectMapper om = new ObjectMapper();
        Map<String, String> map = om.convertValue(user1, Map.class);
        when(userStorage.existsById(user1.getId())).thenReturn(true);
        when(userStorage.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(objectMapper.convertValue(any(User.class), eq(Map.class))).thenReturn(map);
        when(objectMapper.convertValue(any(Map.class), eq(User.class))).thenReturn(user1);
        when(userStorage.save(any(User.class))).thenReturn(user1);

        UserDto actual = userService.update(user1.getId(), userDto);

        assertNotNull(actual);
        assertEquals(user1.getId(), actual.getId());
        assertEquals(user1.getEmail(), actual.getEmail());
        verify(userStorage, times(1)).save(any(User.class));
    }

    @Test
    public void updateUser_whenUserNotFound_thenNotFoundExceptionThrow() {
        doThrow(new NotFoundException(USER_NOT_FOUND)).when(userStorage).existsById(user1.getId());

        NotFoundException actual = assertThrows(NotFoundException.class,
                () -> userService.update(user1.getId(), userDto));

        assertNotNull(actual);
        assertTrue(actual.getMessage().contains("User not found"));
        verify(userStorage, never()).save(any(User.class));
    }

    @Test
    public void getUserById_whenUserFound_thenReturnUser() {
        when(userStorage.existsById(user1.getId())).thenReturn(true);
        when(userStorage.findById(user1.getId())).thenReturn(Optional.of(user1));

        UserDto actual = userService.get(user1.getId());

        assertNotNull(actual);
        assertEquals(user1.getId(), actual.getId());
        assertEquals(user1.getEmail(), actual.getEmail());
    }

    @Test
    public void getUserById_whenUserNotFound_thenNotFoundExceptionThrow() {
        doThrow(new NotFoundException(USER_NOT_FOUND)).when(userStorage).existsById(user1.getId());

        NotFoundException actual = assertThrows(NotFoundException.class,
                () -> userService.get(user1.getId()));

        assertNotNull(actual);
        assertTrue(actual.getMessage().contains("User not found"));
    }

    @Test
    public void deleteUser_thenNothingReturn() {
        when(userStorage.existsById(user1.getId())).thenReturn(true);

        userService.delete(user1.getId());

        verify(userStorage).deleteById(idCaptor.capture());
        assertEquals(user1.getId(), idCaptor.getValue());
    }

    @Test
    public void deleteUser_whenUserNotFound_thenNotFoundExceptionThrow() {
        doThrow(new NotFoundException(USER_NOT_FOUND)).when(userStorage).existsById(user1.getId());

        NotFoundException actual = assertThrows(NotFoundException.class,
                () -> userService.delete(user1.getId()));

        assertNotNull(actual);
        assertTrue(actual.getMessage().contains("User not found"));
    }
}