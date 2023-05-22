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
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.exception.NotFoundException.USER_NOT_FOUND;
import static ru.practicum.shareit.item.controller.ItemController.PARAM_USER_ID;
import static ru.practicum.shareit.request.controller.ItemRequestController.FIRST_PAGE;
import static ru.practicum.shareit.request.controller.ItemRequestController.SIZE_VIEW;

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
    ItemRequestFullDto requestFullDto;

    @BeforeEach
    public void beforeEach() {
        userId = 1L;
        requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("desc");
        requestDto.setCreated(LocalDateTime.now());
        br = new BindException(requestDto, null);
        requestFullDto = new ItemRequestFullDto();
        requestFullDto.setId(requestDto.getId());
        requestFullDto.setCreated(LocalDateTime.now());
        requestFullDto.setDescription("desc");
        requestFullDto.setItems(Collections.emptyList());
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

    @SneakyThrows
    @Test
    public void getAllOwn_thenReturnListRequests() {
        when(requestService.getAllOwn(userId)).thenReturn(List.of(requestFullDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                    .header(PARAM_USER_ID, userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(requestFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].items", empty()));
        verify(requestService, times(1)).getAllOwn(anyLong());
    }

    @SneakyThrows
    @Test
    public void getAllOwn_whenUserNotFound_thenNotFoundExceptionThrow() {
        when(requestService.getAllOwn(userId)).thenThrow(new NotFoundException(USER_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                    .header(PARAM_USER_ID, userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error", containsStringIgnoringCase("user not found")));
        verify(requestService, times(1)).getAllOwn(anyLong());
    }

    @SneakyThrows
    @Test
    public void getAllOwn_whenRequestsNotFound_thenReturnEmptyList() {
        when(requestService.getAllOwn(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header(PARAM_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
        verify(requestService, times(1)).getAllOwn(anyLong());
    }

    @SneakyThrows
    @Test
    public void getAllNotOwn_whenFromLessThen0_thenValidationExceptionThrow() {
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header(PARAM_USER_ID, userId)
                        .param(FIRST_PAGE, String.valueOf(-1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorClass", containsStringIgnoringCase("validateException")))
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("incorrectly")));
        verify(requestService, never()).getAllNotOwn(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    public void getAllNotOwn_whenSizeLessThen1_thenValidationExceptionThrow() {
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header(PARAM_USER_ID, userId)
                        .param(SIZE_VIEW, String.valueOf(0))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorClass", containsStringIgnoringCase("validateException")))
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("incorrectly")));
        verify(requestService, never()).getAllNotOwn(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    public void getAllNotOwn_whenFromAndSizeNotInput_thenReturnListRequests() {
        when(requestService.getAllNotOwn(anyLong(), anyInt(), anyInt())).thenReturn(List.of(requestFullDto));
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header(PARAM_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$.[0].id", is(requestFullDto.getId()), Long.class));
        verify(requestService, times(1)).getAllNotOwn(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    public void getById_thenReturnRequest() {
        when(requestService.getById(userId, requestDto.getId())).thenReturn(requestFullDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", requestDto.getId())
                    .header(PARAM_USER_ID, userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class));
        verify(requestService,times(1)).getById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    public void getById_whenUserNotFound_thenNotFoundExceptionThrow() {
        when(requestService.getById(userId, requestDto.getId())).thenThrow(new NotFoundException(USER_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", requestDto.getId())
                    .header(PARAM_USER_ID, userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorClass", containsStringIgnoringCase("NotFoundException")))
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("User not found")));
        verify(requestService,times(1)).getById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    public void getById_whenUserIdNotInput_thenValidateExceptionThrow() {
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", requestDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorClass", containsStringIgnoringCase("ValidateException")))
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("X-Sharer-User-Id")));
        verify(requestService, never()).getById(anyLong(), anyLong());
    }
}