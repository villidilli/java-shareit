package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    public ItemRequestDto create(@RequestBody ItemRequestDto requestDto, @RequestHeader(PARAM_USER_ID) Long userId) {
        log.debug("create");
        return requestService.create(requestDto, userId);
    }

    @GetMapping
    public List<ItemRequestFullDto> getAllOwn(@RequestHeader(PARAM_USER_ID) Long requesterId) {
        log.debug("/getAllOwn");
        return requestService.getAllOwn(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestFullDto> getAllNotOwn(
            @RequestHeader(PARAM_USER_ID) Long requesterId,
            @RequestParam(value = FIRST_PAGE, defaultValue = DEFAULT_FIRST_PAGE) Integer from,
            @RequestParam(value = SIZE_VIEW, defaultValue = DEFAULT_SIZE_VIEW) Integer size) {
        log.debug("getAllNotOwn");
        return requestService.getAllNotOwn(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestFullDto getById(@RequestHeader(PARAM_USER_ID) Long requesterId,
                                      @PathVariable Long requestId) {
        log.debug("/getById");
        return requestService.getById(requesterId, requestId);
    }
}