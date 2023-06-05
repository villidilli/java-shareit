package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.Constant.*;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(@RequestBody ItemRequestDto requestDto,
                                 @RequestHeader(PARAM_USER_ID) Long userId) {
        log.debug("create");
        return requestService.create(requestDto, userId);
    }

    @GetMapping
    public List<ItemResponseDto> getAllOwn(@RequestHeader(PARAM_USER_ID) Long requesterId) {
        log.debug("/getAllOwn");
        return requestService.getAllOwn(requesterId);
    }

    @GetMapping("/all")
    public List<ItemResponseDto> getAllNotOwn(@RequestHeader(PARAM_USER_ID) Long requesterId,
                                              @RequestParam(FIRST_PAGE) Integer from,
                                              @RequestParam(SIZE_VIEW) Integer size) {
        log.debug("getAllNotOwn");
        return requestService.getAllNotOwn(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemResponseDto getById(@RequestHeader(PARAM_USER_ID) Long requesterId,
                                   @PathVariable Long requestId) {
        log.debug("/getById");
        return requestService.getById(requesterId, requestId);
    }
}