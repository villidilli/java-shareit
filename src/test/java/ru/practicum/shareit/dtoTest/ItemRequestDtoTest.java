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
    JacksonTester<ItemRequestDto> json;

    @Test
    public void jsonToDto() throws IOException {
        String request = "{\"description\" : \"desc\"}";

        ItemRequestDto dto = json.parse(request).getObject();

        assertNotNull(dto);
        assertEquals("desc", dto.getDescription());
        assertNull(dto.getId());
        assertNotNull(dto.getCreated());
    }
}