package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.Constant.PARAM_USER_ID;
import static ru.practicum.shareit.request.controller.ItemRequestController.*;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@Valid @RequestBody ItemDto itemDto,
                          BindingResult br,
                          @RequestHeader(name = PARAM_USER_ID) Long ownerId) {
        log.debug("/create");
        return itemService.create(itemDto, br, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto,
                                    BindingResult br,
                                    @PathVariable Long itemId,
                                    @RequestHeader(PARAM_USER_ID) Long bookerId) {
        log.debug("/createComment");
        return itemService.createComment(commentDto, itemId, bookerId, br);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader(name = PARAM_USER_ID) Long ownerId) {
        log.debug("/update");
        return itemService.update(itemId, itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking get(@PathVariable Long itemId,
                                  @RequestHeader(name = PARAM_USER_ID) Long ownerId) {
        log.debug("/get");
        return itemService.get(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDtoWithBooking> getByOwner(
            @RequestHeader(name = PARAM_USER_ID) Long ownerId,
            @RequestParam(value = FIRST_PAGE, defaultValue = DEFAULT_FIRST_PAGE) @PositiveOrZero Integer from,
            @RequestParam(value = SIZE_VIEW, defaultValue = DEFAULT_SIZE_VIEW) @Positive Integer size) {
        log.debug("/getByOwner");
        return itemService.getByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestParam String text,
            @RequestParam(value = FIRST_PAGE, defaultValue = DEFAULT_FIRST_PAGE) @PositiveOrZero Integer from,
            @RequestParam(value = SIZE_VIEW, defaultValue = DEFAULT_SIZE_VIEW) @Positive Integer size) {
        log.debug("/search");
        return itemService.search(text, from, size);
    }
}