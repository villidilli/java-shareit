package ru.practicum.shareit.dtoTest;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    public void jsonToDto() throws IOException {
        String body = "{\"name\" : \"name\", \"description\" : \"desc\", \"available\" : \"true\"}";

        ItemDto dto = json.parse(body).getObject();

        assertNotNull(dto);
        assertEquals("name", dto.getName());
        assertEquals("desc", dto.getDescription());
        assertEquals(Boolean.TRUE, dto.getAvailable());
    }
}