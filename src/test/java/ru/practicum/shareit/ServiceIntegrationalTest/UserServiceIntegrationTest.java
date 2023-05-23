package ru.practicum.shareit.ServiceIntegrationalTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Transactional
@SpringBootTest(properties = "spring.datasource.url = jdbc:h2:mem:test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    @Autowired
    UserService userService;
    UserDto userDto;
    BindingResult br;

    @BeforeEach
    public void beforeEach() {
        userDto = new UserDto(null,"name", "email");
        br = new BindException(userDto, null);
    }
    @Test
    public void c() {
        UserDto savedUser = userService.create(userDto, br);
        Assertions.assertEquals(1L, savedUser.getId());
    }
}
