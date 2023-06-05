package ru.practicum.shareit.controllerTest;

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
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.exception.NotFoundException.USER_NOT_FOUND;

@WebMvcTest(controllers = UserController.class)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    private UserDto userDto;
    private BindingResult br;

    @BeforeEach
    public void beforeEach() {
        userDto = new UserDto(1L, "name", "user@user.ru");
        br = new BindException(userDto, null);
    }

    @SneakyThrows
    @Test
    public void getUserById_thenReturnUser() {
        Long userID = 1L;

        when(userService.get(any(Long.class))).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}", userID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        verify(userService, times(1)).get(anyLong());
    }

    @SneakyThrows
    @Test
    public void getUserById_whenUserIdNull_thenNotFoundExceptionThrow() {
        doThrow(new NotFoundException(USER_NOT_FOUND)).when(userService).get(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}", anyLong())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto))
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("user not found")));
        verify(userService, times(1)).get(anyLong());
    }

    @SneakyThrows
    @Test
    public void createUser_thenReturnUser() {
        when(userService.create(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        verify(userService, times(1)).create(any(UserDto.class));
    }

    @SneakyThrows
    @Test
    public void updateUser_thenReturnUser() {
        UserDto expectedUser = new UserDto();
        expectedUser.setId(userDto.getId());
        expectedUser.setEmail(userDto.getEmail());
        expectedUser.setName("updatedName");

        when(userService.update(anyLong(), any(UserDto.class))).thenReturn(expectedUser);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", userDto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto))
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(expectedUser.getName())));
        verify(userService, times(1)).update(anyLong(), any(UserDto.class));
    }

    @SneakyThrows
    @Test
    public void updateUser_whenUserNull_thenNotFoundExceptionThrow() {
        doThrow(new NotFoundException(USER_NOT_FOUND)).when(userService).update(anyLong(), any(UserDto.class));

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", userDto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto))
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("user not found")));
        verify(userService, times(1)).update(anyLong(), any(UserDto.class));
    }

    @SneakyThrows
    @Test
    public void deleteUserById_thenReturnNothing() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", userDto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto))
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").doesNotHaveJsonPath());
        verify(userService, times(1)).delete(anyLong());
    }

    @SneakyThrows
    @Test
    public void getAllUsers_thenReturnListUsers() {
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
    }
}