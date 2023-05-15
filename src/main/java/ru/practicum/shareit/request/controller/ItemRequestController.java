package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;

import static ru.practicum.shareit.item.controller.ItemController.PARAM_USER_ID;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(@Valid @RequestBody ItemRequestDto requestDto,
                                 BindingResult br,
                                 @RequestHeader(name = PARAM_USER_ID) Long userId) {
        log.debug("create");
        return requestService.create(requestDto, br, userId);
    }

//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    public ItemRequestDto getByUser(@RequestHeader(name = PARAM_USER_ID) Long requesterId) {
//        log.debug("/getByUser");
//        return requestService.getByRequester(requesterId);
//    }
}