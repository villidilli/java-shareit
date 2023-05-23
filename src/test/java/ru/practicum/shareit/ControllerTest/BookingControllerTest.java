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
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.controller.BookingController.PARAM_APPROVED;
import static ru.practicum.shareit.exception.NotFoundException.BOOKING_NOT_FOUND;
import static ru.practicum.shareit.exception.NotFoundException.USER_NOT_FOUND;
import static ru.practicum.shareit.item.controller.ItemController.PARAM_USER_ID;

@WebMvcTest(controllers = BookingController.class)
@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    BookingService bookingService;

    BookingResponseDto responseDto;
    User user;
    BookingResponseDto.UserShortDto booker;
    BindingResult br;
    BookingRequestDto requestDto;
    Item item;

    @BeforeEach
    public void beforeEach() {
        item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setAvailable(true);
        item.setOwner(user);

        user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@user.ru");

        booker = new BookingResponseDto.UserShortDto(user.getId());

        responseDto = new BookingResponseDto();
        responseDto.setId(1L);
        responseDto.setBooker(booker);
        responseDto.setStart(LocalDateTime.now().plusMinutes(10));
        responseDto.setEnd(LocalDateTime.now().plusHours(1));

        requestDto = new BookingRequestDto();
        requestDto.setItemId(item.getId());
        requestDto.setStart(LocalDateTime.now().plusMinutes(10));
        requestDto.setEnd(LocalDateTime.now().plusHours(1));

        br = new BindException(requestDto, null);
    }

    @SneakyThrows
    @Test
    public void createBooking_thenReturnBooking() {
        when(bookingService.create(any(), any(), any())).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .header(PARAM_USER_ID, user.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class));
        verify(bookingService, times(1)).create(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void createBooking_whenBookerNotFound_thenNotFoundExceptionThrow() {
        when(bookingService.create(any(), any(), any())).thenThrow(new NotFoundException(USER_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header(PARAM_USER_ID, user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorClass", containsStringIgnoringCase("NotFoundException")))
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("User not found")));
        verify(bookingService, times(1)).create(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void createBooking_whenBookerIdParamNotTransferred_thenValidateExceptionThrow() {
        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorClass", containsStringIgnoringCase("ValidateException")))
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("X-Sharer-User-Id")));
        verify(bookingService, never()).create(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void updateBooking_thenReturnUpdatedBooking() {
        when(bookingService.update(any(), any(), any())).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/{bookingId}", responseDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header(PARAM_USER_ID, user.getId())
                        .param(PARAM_APPROVED, String.valueOf(true))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class));
        verify(bookingService, times(1)).update(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void updateBooking_whenBookingNotFoundthenNotFoundExceptionThrow() {
        when(bookingService.update(any(), any(), any())).thenThrow(new NotFoundException(BOOKING_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/{bookingId}", responseDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header(PARAM_USER_ID, user.getId())
                        .param(PARAM_APPROVED, String.valueOf(true))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorClass", containsStringIgnoringCase("NotFoundException")))
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("Booking not found")));
        verify(bookingService, times(1)).update(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void updateBooking_whenHeaderUserIdNotTransferred_thenValidateExceptionThrow() {
        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/{bookingId}", responseDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .param(PARAM_APPROVED, String.valueOf(true))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorClass", containsStringIgnoringCase("ValidateException")))
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("X-Sharer-User-Id")));
        verify(bookingService, never()).update(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void updateBooking_whenParamStatusNotTransferred_thenValidateExceptionThrow() {
        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/{bookingId}", responseDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header(PARAM_USER_ID, user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorClass", containsStringIgnoringCase("ValidateException")))
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("Status")));
        verify(bookingService, never()).update(any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void getByUser_thenReturnBooking() {
        when(bookingService.getByUser(any(), any())).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", responseDto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(PARAM_USER_ID, user.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class));
        verify(bookingService, times(1)).getByUser(any(), any());
    }

    @SneakyThrows
    @Test
    public void getByUser_whenBookingNotFoundthenNotFoundExceptionThrow() {
        when(bookingService.getByUser(any(), any())).thenThrow(new NotFoundException(BOOKING_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", responseDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(PARAM_USER_ID, user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorClass", containsStringIgnoringCase("NotFoundException")))
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("Booking not found")));
        verify(bookingService, times(1)).getByUser(any(), any());
    }

    @SneakyThrows
    @Test
    public void getByUser_whenHeaderUserIdNotTransferred_thenValidateExceptionThrow() {
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", responseDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorClass", containsStringIgnoringCase("ValidateException")))
                .andExpect(jsonPath("$.error", containsStringIgnoringCase("X-Sharer-User-Id")));
        verify(bookingService, never()).getByUser(any(), any());
    }

    @SneakyThrows
    @Test
    public void getAllByBooker_whenFromAndSizeDefault_thenReturnListBookings() {
        when(bookingService.getAllByBooker(any(), any(), any(), any())).thenReturn(List.of(responseDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(PARAM_USER_ID, user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$.[0].id", is(responseDto.getId()), Long.class));
        verify(bookingService, times(1)).getAllByBooker(any(), any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void getAllByOwner_whenFromAndSizeDefault_thenReturnListBookings() {
        when(bookingService.getAllByOwner(any(), any(), any(), any())).thenReturn(List.of(responseDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(PARAM_USER_ID, user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$.[0].id", is(responseDto.getId()), Long.class));
        verify(bookingService, times(1)).getAllByOwner(any(), any(), any(), any());
    }
}