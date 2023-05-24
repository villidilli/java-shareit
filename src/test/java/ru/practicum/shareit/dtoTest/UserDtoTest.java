package ru.practicum.shareit.dtoTest;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class UserDtoTest {
    @Autowired
    JacksonTester<UserDto> json;

    @Test
    public void jsonToDto() throws IOException {
        String request = "{\"name\" : \"user\", \"email\" : \"user@email.ru\"}";

        UserDto dto = json.parse(request).getObject();

        assertNotNull(dto);
        assertEquals("user", dto.getName());
        assertEquals("user@email.ru", dto.getEmail());
        assertNull(dto.getId());
    }
}