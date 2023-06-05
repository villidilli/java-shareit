package ru.practicum.shareit.dtoTest;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    public void jsonToDto() throws IOException {
        String body = "{\"description\" : \"desc\"}";

        ItemRequestDto dto = json.parse(body).getObject();

        assertNotNull(dto);
        assertEquals("desc", dto.getDescription());
        assertNull(dto.getId());
    }
}