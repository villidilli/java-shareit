package ru.practicum.shareit.dtoTest;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import ru.practicum.shareit.item.dto.CommentDto;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class CommentDtoTest {
    @Autowired
    JacksonTester<CommentDto> json;

    @Test
    public void jsonToDto() throws IOException {
        String body = "{\"text\":\"comment\"}";

        CommentDto dto = json.parse(body).getObject();

        assertNotNull(dto);
        assertEquals("comment", dto.getText());
        assertNull(dto.getAuthorName());
        assertNull(dto.getId());
    }
}