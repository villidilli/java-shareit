package ru.practicum.shareit.serviceIntegrationalTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(properties = "spring.datasource.url = jdbc:h2:mem:test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {
    @Autowired
    ItemRequestService requestService;
    @Autowired
    UserService userService;
    @Autowired
    ItemService itemService;

    @Test
    public void getAllOwn() {
        UserDto owner1 = new UserDto(null, "owner1", "owner@email.ru");
        UserDto requester = new UserDto(null, "requester", "requester@email.ru");
        UserDto savedOwner1 = userService.create(owner1, new BindException(owner1, null));
        UserDto savedRequester = userService.create(requester, new BindException(requester, null));

        ItemRequestDto request = new ItemRequestDto(null, "desc", LocalDateTime.now());
        ItemRequestDto savedRequest = requestService.create(request, new BindException(request, null), savedRequester.getId());

        ItemDto item1 = new ItemDto(null, "item1", "desc", true, null, savedRequest.getId());
        ItemDto savedItem1 = itemService.create(item1, new BindException(item1, null), savedOwner1.getId());

        List<ItemRequestFullDto> actualList = requestService.getAllOwn(savedRequester.getId());
        assertNotNull(actualList);
        assertEquals(1, actualList.size());
        assertEquals(savedRequest.getId(), actualList.get(0).getId());
        assertEquals(savedItem1.getId(), actualList.get(0).getItems().get(0).getId());
    }
}