package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto requestDto, Long userId);

    void isExist(Long requestId);

    List<ItemResponseDto> getAllOwn(Long requesterId);

    List<ItemResponseDto> getAllNotOwn(Long requesterId, Integer from, Integer size);

    ItemResponseDto getById(Long requesterId, Long requestId);
}