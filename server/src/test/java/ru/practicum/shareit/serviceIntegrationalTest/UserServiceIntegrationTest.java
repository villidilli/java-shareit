package ru.practicum.shareit.serviceIntegrationalTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(properties = "spring.datasource.url = jdbc:h2:mem:test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    private UserDto userDto;
    private UserDto userDto1;

    @BeforeEach
    public void beforeEach() {
        userDto = new UserDto(null,"user", "user@email.ru");
        userDto1 = new UserDto(null,"user1", "user1@email.ru");
    }

    @Test
    public void getAllUsers_thenReturnListUsers() {
        userService.create(userDto);
        userService.create(userDto1);
        List<UserDto> expectedList = List.of(userDto, userDto1);

        List<UserDto> actualList = userService.getAll();
        assertEquals(expectedList.size(), actualList.size());
        assertNotNull(actualList.get(0).getId());
        assertNotNull(actualList.get(1).getId());
    }
}