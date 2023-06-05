package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.Constant.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/items")
public class ItemController {
    public static final String PARAM_USER_ID = "X-Sharer-User-Id";
    private final ItemClient client;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                                 BindingResult br,
                                 @RequestHeader(name = PARAM_USER_ID) Long ownerId) {
        log.debug("[GATEWAY]/create");
        return client.create(itemDto, br, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto commentDto,
                                                BindingResult br,
                                                @PathVariable Long itemId,
                                                @RequestHeader(PARAM_USER_ID) Long bookerId) {
        log.debug("[GATEWAY]/createComment");
        return client.createComment(commentDto, itemId, bookerId, br);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable Long itemId,
                                         @RequestBody ItemDto itemDto,
                                         @RequestHeader(name = PARAM_USER_ID) Long ownerId) {
        log.debug("[GATEWAY]/update");
        return client.update(itemId, itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable Long itemId,
                                  @RequestHeader(name = PARAM_USER_ID) Long ownerId) {
        log.debug("[GATEWAY]/get");
        return client.get(itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getByOwner(
            @RequestHeader(name = PARAM_USER_ID) Long ownerId,
            @RequestParam(value = FIRST_PAGE, defaultValue = DEFAULT_FIRST_PAGE) @PositiveOrZero Integer from,
            @RequestParam(value = SIZE_VIEW, defaultValue = DEFAULT_SIZE_VIEW) @Positive Integer size) {
        log.debug("[GATEWAY]/getByOwner");
        return client.getByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestHeader(name = PARAM_USER_ID) Long userId,
            @RequestParam String text,
            @RequestParam(value = FIRST_PAGE, defaultValue = DEFAULT_FIRST_PAGE) @PositiveOrZero Integer from,
            @RequestParam(value = SIZE_VIEW, defaultValue = DEFAULT_SIZE_VIEW) @Positive Integer size) {
        log.debug("/search");
        return client.search(userId, text, from, size);
    }
}