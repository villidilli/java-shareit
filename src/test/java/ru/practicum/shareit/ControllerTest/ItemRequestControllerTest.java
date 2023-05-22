package ru.practicum.shareit.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.exception.NotFoundException.USER_NOT_FOUND;
import static ru.practicum.shareit.item.controller.ItemController.PARAM_USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ItemRequestService requestService;

    ItemRequestDto requestDto;
    BindingResult br;
    Long userId;

    @BeforeEach
    public void beforeEach() {
        userId = 1L;
        requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("desc");
        requestDto.setCreated(LocalDateTime.now());
        br = new BindException(requestDto, null);
    }

    @SneakyThrows
    @Test
    public void createRequest_thenReturnRequest() {
        when(requestService.create(any(ItemRequestDto.class), any(BindingResult.class), anyLong()))
                .thenReturn(requestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .header(PARAM_USER_ID, userId)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class));
        verify(requestService, times(1)).create(any(), any(), anyLong());
    }

    @SneakyThrows
    @Test
    public void createRequest_whenUserNotFound_thenNotFoundExceptionThrow() {
        when(requestService.create(any(ItemRequestDto.class), any(BindingResult.class), anyLong()))
                .thenThrow(new NotFoundException(USER_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .header(PARAM_USER_ID, userId)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error",
                                                            containsStringIgnoringCase("user not found")));
        verify(requestService, times(1))
                .create(any(ItemRequestDto.class), any(BindingResult.class), anyLong());
    }
}
