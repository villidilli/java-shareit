package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.Constant.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestBody ItemDto itemDto,
                          @RequestHeader(PARAM_USER_ID) Long ownerId) {
        log.debug("/create");
        return itemService.create(itemDto, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @PathVariable Long itemId,
                                    @RequestHeader(PARAM_USER_ID) Long bookerId) {
        log.debug("/createComment");
        return itemService.createComment(commentDto, itemId, bookerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader(PARAM_USER_ID) Long ownerId) {
        log.debug("/update");
        return itemService.update(itemId, itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking get(@PathVariable Long itemId,
                                  @RequestHeader(PARAM_USER_ID) Long ownerId) {
        log.debug("/get");
        return itemService.get(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDtoWithBooking> getByOwner(@RequestHeader(PARAM_USER_ID) Long ownerId,
                                               @RequestParam(FIRST_PAGE) Integer from,
                                               @RequestParam(SIZE_VIEW) Integer size) {
        log.debug("/getByOwner");
        return itemService.getByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam(FIRST_PAGE) Integer from,
                                @RequestParam(SIZE_VIEW) Integer size) {
        log.debug("/search");
        return itemService.search(text, from, size);
    }
}