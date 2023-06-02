package ru.practicum.shareit.request.service;

import org.springframework.validation.BindingResult;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto requestDto, Long userId);

    void isExist(Long requestId);

    List<ItemRequestFullDto> getAllOwn(Long requesterId);

    List<ItemRequestFullDto> getAllNotOwn(Long requesterId, Integer from, Integer size);

    ItemRequestFullDto getById(Long requesterId, Long requestId);
}