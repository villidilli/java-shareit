package ru.practicum.shareit.dtoTest;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@JsonTest
public class BookingRequestDtoTest {
    @Autowired
    private JacksonTester<BookingRequestDto> json;

    @Test
    public void jsonToDto() throws IOException {
        String body =
                "{\"start\" : \"2023-05-25T00:00:00\", \"end\" : \"2023-05-27T00:00:00\"}";

        BookingRequestDto dto = json.parse(body).getObject();

        assertNull(dto.getItemId());
        assertEquals(LocalDateTime.of(2023, 5,25, 0,0,0), dto.getStart());
        assertEquals(LocalDateTime.of(2023, 5,27, 0,0,0), dto.getEnd());
        assertNull(dto.getItemId());
    }
}