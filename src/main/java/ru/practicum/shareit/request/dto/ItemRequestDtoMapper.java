package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ItemRequestDtoMapper {
    public static ItemRequest toItemRequest(ItemRequestDto requestDto, User requester) {
        log.debug("/toItemRequest");
        ItemRequest request = new ItemRequest();
        request.setDescription(requestDto.getDescription());
        request.setRequester(requester);
        request.setCreated(requestDto.getCreated());
        return request;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        log.debug("/toItemRequestDto");
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());
        requestDto.setCreated(request.getCreated());
        return requestDto;
    }

    public static ItemRequestFullDto toItemRequestDtoWithItem(ItemRequest request, List<Item> items) {
        log.debug("/toItemRequestDtoWithItem");
        log.debug("ПРИЛЕТЕЛИ ИТЕМЫ " + items);
        ItemRequestFullDto requestDto = new ItemRequestFullDto();
        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());
        requestDto.setCreated(request.getCreated());
        List<ItemRequestFullDto.ItemShortDto> itemShortDtos = new ArrayList<>();
        if (items != null) {
            itemShortDtos = items.stream()
                    .map(ItemRequestDtoMapper::toItemShortDto)
                    .collect(Collectors.toList());
            log.debug("ИТЕМ САЙЗ = 0");
        }
        requestDto.setItems(itemShortDtos);
        return requestDto;
    }

    public static ItemRequestFullDto.ItemShortDto toItemShortDto(Item item) {
        log.debug("/toItemShortDto");
        return new ItemRequestFullDto.ItemShortDto(item.getId(),
                item.getName(), item.getDescription(), item.getAvailable(), item.getRequest().getId());
    }
}