package ru.practicum.shareit.serviceIntegrationalTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
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
    private ItemRequestService requestService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    @Test
    public void getAllOwn() {
        UserDto owner1 = new UserDto(null, "owner1", "owner@email.ru");
        UserDto requester = new UserDto(null, "requester", "requester@email.ru");
        UserDto savedOwner1 = userService.create(owner1);
        UserDto savedRequester = userService.create(requester);

        ItemRequestDto request = new ItemRequestDto(null, "desc", LocalDateTime.now());
        ItemRequestDto savedRequest =
                requestService.create(request, savedRequester.getId());

        ItemDto item1 = new ItemDto(
                null, "item1", "desc", true, null, savedRequest.getId());
        ItemDto savedItem1 = itemService.create(item1, savedOwner1.getId());

        List<ItemResponseDto> actualList = requestService.getAllOwn(savedRequester.getId());

        assertNotNull(actualList);
        assertEquals(1, actualList.size());
        assertEquals(savedRequest.getId(), actualList.get(0).getId());
        assertEquals(savedItem1.getId(), actualList.get(0).getItems().get(0).getId());
    }
}