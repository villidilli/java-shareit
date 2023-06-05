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

    public static ItemResponseDto toItemRequestDtoWithItem(ItemRequest request, List<Item> items) {
        log.debug("/toItemRequestDtoWithItem");
        ItemResponseDto requestDto = new ItemResponseDto();
        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());
        requestDto.setCreated(request.getCreated());
        List<ItemResponseDto.ItemShortDto> itemShortDtos = new ArrayList<>();
        if (items != null) {
            itemShortDtos = items.stream()
                    .map(ItemRequestDtoMapper::toItemShortDto)
                    .collect(Collectors.toList());
        }
        requestDto.setItems(itemShortDtos);
        return requestDto;
    }

    public static ItemResponseDto.ItemShortDto toItemShortDto(Item item) {
        log.debug("/toItemShortDto");
        return new ItemResponseDto.ItemShortDto(item.getId(),
                item.getName(), item.getDescription(), item.getAvailable(), item.getRequest().getId());
    }
}