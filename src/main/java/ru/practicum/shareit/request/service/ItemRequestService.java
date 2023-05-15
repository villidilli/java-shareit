package ru.practicum.shareit.request.service;

import org.springframework.validation.BindingResult;
import ru.practicum.shareit.request.dto.ItemRequestDto;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto requestDto, BindingResult br, Long userId);

    ItemRequestDto getByRequester(Long requesterId);
}