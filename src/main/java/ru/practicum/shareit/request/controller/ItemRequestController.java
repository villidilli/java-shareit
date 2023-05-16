package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;

import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.PARAM_USER_ID;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    public static final String NUM_FIRST_ELEM = "from";
    public static final String SIZE_VIEW = "size";
    private final ItemRequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(@Valid @RequestBody ItemRequestDto requestDto,
                                 BindingResult br,
                                 @RequestHeader(PARAM_USER_ID) Long userId) {
        log.debug("create");
        return requestService.create(requestDto, br, userId);
    }

    @GetMapping
    public List<ItemRequestFullDto> getAllOwn(@RequestHeader(PARAM_USER_ID) Long requesterId) {
        log.debug("/getAllOwn");
        return requestService.getAllOwn(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestFullDto> getAllNotOwn(@RequestHeader(PARAM_USER_ID) Long requesterId,
                                                 @RequestParam(value = NUM_FIRST_ELEM, required = false) Integer from,
                                                 @RequestParam(value = SIZE_VIEW, required = false) Integer size) {
        log.debug("getAllNotOwn");
        return requestService.getAllNotOwn(requesterId);
    }
}