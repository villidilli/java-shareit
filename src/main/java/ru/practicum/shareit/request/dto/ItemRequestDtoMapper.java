package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

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
}