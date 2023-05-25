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

import static ru.practicum.shareit.item.controller.ItemController.PARAM_USER_ID;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemRequestController {
    public static final String FIRST_PAGE = "from";
    public static final String DEFAULT_FIRST_PAGE = "0";
    public static final String SIZE_VIEW = "size";
    public static final String DEFAULT_SIZE_VIEW = "999";
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
    public List<ItemRequestFullDto> getAllNotOwn(
            @RequestHeader(PARAM_USER_ID) Long requesterId,
            @RequestParam(value = FIRST_PAGE, defaultValue = DEFAULT_FIRST_PAGE) @PositiveOrZero Integer from,
            @RequestParam(value = SIZE_VIEW, defaultValue = DEFAULT_SIZE_VIEW) @Positive Integer size) {
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