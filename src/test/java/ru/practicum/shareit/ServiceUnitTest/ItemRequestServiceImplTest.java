package ru.practicum.shareit.ServiceUnitTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.exception.NotFoundException.REQUEST_NOT_FOUND;
import static ru.practicum.shareit.exception.NotFoundException.USER_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    UserService userService;
    @Mock
    UserStorage userStorage;
    @Mock
    ItemStorage itemStorage;
    @Mock
    ItemRequestStorage requestStorage;
    @Mock
    ItemRequestServiceImpl mockRequestService;

    @InjectMocks
    ItemRequestServiceImpl requestService;

    ItemRequestDto requestDto1;
    LocalDateTime date1;
    BindingResult br;
    User user1;
    ItemRequest request;

    @BeforeEach
    public void beforeEach() {
        user1 = new User(1L, "name", "user1@user.ru");
        date1 = LocalDateTime.of(2023, 5, 19, 0, 0);
        requestDto1 =
                new ItemRequestDto(1L, "desc", date1);
        br = new BindException(requestDto1, null);
        request = new ItemRequest(1L, "desc", user1, date1);
    };

    @Test
    public void createRequest_thenReturnCreatedRequest() {
        when(requestStorage.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto actual = requestService.create(requestDto1, br, user1.getId());

        assertNotNull(actual);
        assertEquals(request.getId(), actual.getId());
        assertEquals(request.getDescription(), actual.getDescription());
        verify(requestStorage, times(1)).save(any(ItemRequest.class));
    }

    @Test
    public void createRequest_whenUserNotFound_thenNotFoundExceptionThrow() {
        doThrow(new NotFoundException(USER_NOT_FOUND)).when(userService).isExist(anyLong());

        NotFoundException actual = assertThrows(NotFoundException.class,
                () -> requestService.create(requestDto1, br, user1.getId()));

        assertNotNull(actual);
        assertTrue(actual.getMessage().contains("User not found"));
        verify(requestStorage, never()).save(any(ItemRequest.class));
    }

    @Test
    public void getAllOwn_whenRequestsNotCreated_thenReturnEmptyList() {
        List<ItemRequestFullDto> actual = requestService.getAllOwn(user1.getId());

        assertTrue(actual.isEmpty());
    }

    @Test
    public void getAllOwn_whenRequestsFound_thenReturnListRequests() {
        List<ItemRequest> expectedList = List.of(request);
        when(requestStorage.findByRequester_Id(user1.getId(),
                Sort.by("created").descending())).thenReturn(expectedList);

        List<ItemRequestFullDto> actual = requestService.getAllOwn(user1.getId());

        assertNotNull(actual);
        assertEquals(expectedList.size(), actual.size());
        assertEquals(expectedList.get(0).getId(), actual.get(0).getId());
        assertEquals(expectedList.get(0).getDescription(), actual.get(0).getDescription());
    }

    @Test
    public void getAllOwn_whenUserNotFound_thenNotFoundExceptionThrow() {
        doThrow(new NotFoundException(USER_NOT_FOUND))
                .when(userService).isExist(anyLong());

        NotFoundException actual = assertThrows(NotFoundException.class,
                () -> requestService.getAllOwn(user1.getId()));

        assertNotNull(actual);
        assertTrue(actual.getMessage().contains("User not found"));
    }

    @Test
    public void getAllNotOwn_whenUserNotFound_thenNotFoundExceptionThrow() {
        doThrow(new NotFoundException(USER_NOT_FOUND))
                .when(userService).isExist(anyLong());

        NotFoundException actual = assertThrows(NotFoundException.class,
                () -> requestService.getAllNotOwn(user1.getId(), 0, 999));

        assertNotNull(actual);
        assertTrue(actual.getMessage().contains("User not found"));
    }

    @Test
    public void getAllOwn_whenRequestNotCreated_thenReturnEmptyList() {
        Pageable page = PageRequest.of(0, 999, Sort.by("created").descending());
        when(requestStorage.findByRequester_IdNot(user1.getId(), page))
                .thenReturn(Page.empty());

        List<ItemRequestFullDto> actual =
                requestService.getAllNotOwn(user1.getId(), 0, 999);

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void getAllOwn_whenRequestFound_thenReturnListRequests() {
        List<ItemRequest> expected = List.of(request);
        Pageable page = PageRequest.of(1, 1, Sort.by("created").descending());
        when(requestStorage.findByRequester_IdNot(user1.getId(), page))
                .thenReturn(new PageImpl<>(expected));

        List<ItemRequestFullDto> actual = requestService.getAllNotOwn(user1.getId(), 1, 1);

        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        assertEquals(expected.get(0).getId(), actual.get(0).getId());
    }

    @Test
    public void isExistRequest_whenRequestNotFound_thenNotFoundExceptionThrow() {
        doThrow(new NotFoundException(REQUEST_NOT_FOUND))
                .when(requestStorage).existsById(anyLong());

        NotFoundException actual = assertThrows(NotFoundException.class,
                () -> requestService.isExist(request.getId()));

        assertNotNull(actual);
        assertTrue(actual.getMessage().contains("Request not found"));
    }

    @Test
    public void getById_whenUserNotFound_thenNotFoundExceptionThrow() {
        when(requestStorage.existsById(anyLong())).thenReturn(true);
        doThrow(new NotFoundException(USER_NOT_FOUND))
                .when(userService).isExist(anyLong());

        NotFoundException actual = assertThrows(NotFoundException.class,
                () -> requestService.getById(user1.getId(), request.getId()));

        assertNotNull(actual);
        assertTrue(actual.getMessage().contains("User not found"));
    }

    @Test
    public void getById_whenRequestFound_thenReturnRequest() {
        when(requestStorage.existsById(anyLong())).thenReturn(true);
        when(requestStorage.findByIdIs(request.getId())).thenReturn(request);

        ItemRequestFullDto actual = requestService.getById(user1.getId(), request.getId());

        assertNotNull(actual);
        assertEquals(request.getId(), actual.getId());
        assertEquals(request.getDescription(), actual.getDescription());
    }
}