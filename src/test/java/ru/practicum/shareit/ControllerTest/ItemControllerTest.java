package ru.practicum.shareit.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.hamcrest.HamcrestArgumentMatcher;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.exception.NotFoundException.*;
import static ru.practicum.shareit.item.controller.ItemController.PARAM_USER_ID;

@WebMvcTest(controllers = ItemController.class)
@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ItemService itemService;

    ItemDto itemDto;
    CommentDto commentDto;
    ItemDtoWithBooking itemDtoWithBooking;
    BindingResult br;
    User user;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@user.ru");

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setCreated(LocalDateTime.now());
        commentDto.setText("comment");
        commentDto.setAuthorName(user.getName());

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("desc");
        itemDto.setComments(List.of(commentDto));
        itemDto.setAvailable(true);

        itemDtoWithBooking = new ItemDtoWithBooking();
        itemDtoWithBooking.setId(1L);
        itemDtoWithBooking.setName("itemDtoWithBooking");
        itemDtoWithBooking.setAvailable(true);

        br = new BindException(itemDto, null);
    }

    @SneakyThrows
    @Test
    public void createItem_thenReturnItem() {
        ItemDto expectedDto = new ItemDto();
        expectedDto.setId(2L);

        when(itemService.create(any(ItemDto.class), any(BindingResult.class), anyLong())).thenReturn(expectedDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(itemDto))
                    .header(PARAM_USER_ID, user.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(expectedDto.getId()), Long.class));
        verify(itemService, times(1)).create(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void createItem_whenUserIdNotInput_thenValidateExceptionThrow() {
        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorClass", containsStringIgnoringCase("validateException")))
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("X-Sharer-User-ID")));
        verify(itemService, never()).create(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void createComment_thenReturnComment() {
        when(itemService.createComment(any(CommentDto.class), anyLong(), anyLong(), any(BindingResult.class)))
                .thenReturn(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", itemDto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(commentDto))
                    .header(PARAM_USER_ID, user.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class));
        verify(itemService, times(1)).createComment(any(), any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void createComment_whenItemNotFound_thenNotFoundExceptionThrow() {
        when(itemService.createComment(any(), any(), any(), any())).thenThrow(new NotFoundException(ITEM_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", itemDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header(PARAM_USER_ID, user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("errorClass", containsStringIgnoringCase("NotFoundException")))
                .andExpect(jsonPath("error", containsStringIgnoringCase("Item not found")));
        verify(itemService, times(1)).createComment(any(), any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void createComment_whenUserIdNotInput_thenValidateExceptionThrow() {
        mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", itemDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorClass", containsStringIgnoringCase("ValidateException")))
                .andExpect(jsonPath("error", containsStringIgnoringCase("X-Sharer-User-ID")));
        verify(itemService, never()).createComment(any(), any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void createComment_whenBodyNull_thenServerErrorExceptionThrow() {
        mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", itemDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(PARAM_USER_ID, user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("error", containsStringIgnoringCase("body is missing")));
        verify(itemService, never()).createComment(any(), any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void updateItem_thenReturnItem() {
        ItemDto expected = new ItemDto();
        expected.setId(1L);
        expected.setDescription("new desc");
        when(itemService.update(any(), any(), any())).thenReturn(expected);

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", itemDto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(itemDto))
                    .header(PARAM_USER_ID, user.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expected.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(expected.getDescription())));
        verify(itemService,times(1)).update(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void updateItem_whenItemNotFound_thenNotFoundExceptionThrow() {
        when(itemService.update(any(), any(), any())).thenThrow(new NotFoundException(ITEM_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", itemDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(PARAM_USER_ID, user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorClass", containsStringIgnoringCase("NotFoundException")))
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("Item not found")));
        verify(itemService,times(1)).update(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void updateItem_whenUserNotOwnerItem_thenNotFoundExceptionThrow() {
        when(itemService.update(any(), any(), any())).thenThrow(new NotFoundException(OWNER_NOT_MATCH_ITEM));

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", itemDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header(PARAM_USER_ID, user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorClass", containsStringIgnoringCase("NotFoundException")))
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("Owner does not match")));
        verify(itemService,times(1)).update(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void getById_thenReturnItem() {
        when(itemService.get(any(), any())).thenReturn(itemDtoWithBooking);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemDto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(PARAM_USER_ID, user.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("itemDtoWithBooking")));
        verify(itemService, times(1)).get(any(), any());
    }

    @SneakyThrows
    @Test
    public void getById_whenItemNotFound_thenNotFoundExceptionThrow() {
        when(itemService.get(any(), any())).thenThrow(new NotFoundException(ITEM_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(PARAM_USER_ID, user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorClass", containsStringIgnoringCase("NotFoundException")))
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("Item not found")));
        verify(itemService, times(1)).get(any(), any());
    }

    @SneakyThrows
    @Test
    public void getById_whenUserIdNotInput_whenValidateExceptionThrow() {
        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemDto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorClass", containsStringIgnoringCase("ValidateException")))
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("X-Sharer-User-Id")));
        verify(itemService, never()).get(any(), any());
    }

    @SneakyThrows
    @Test
    public void getByOwner_whenDefaultFromSize_thenReturnListItems() {
        when(itemService.getByOwner(any(), any(), any())).thenReturn(List.of(itemDtoWithBooking));

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(PARAM_USER_ID, user.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$.[0].name", is(itemDtoWithBooking.getName())));
        verify(itemService, times(1)).getByOwner(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void getByOwner_whenUserNotFound_thenNotFoundExceptionThrow() {
        when(itemService.getByOwner(any(), any(), any())).thenThrow(new NotFoundException(USER_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(PARAM_USER_ID, user.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorClass", containsStringIgnoringCase("NotFoundException")))
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("User not found")));
        verify(itemService, times(1)).getByOwner(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void search_whenDefaultPage_thenReturnListItem() {
        when(itemService.search(any(), any(), any())).thenReturn(List.of(itemDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("text", "textForFind")
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())));
        verify(itemService, times(1)).search(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void search_whenTextBlank_thenReturnEmptyList() {
        when(itemService.search(any(), any(), any())).thenReturn(Collections.emptyList());
        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", "")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());

        verify(itemService, times(1)).search(any(), any(), any());
    }
}