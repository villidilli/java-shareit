package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    public static final String PARAM_NAME_OWNER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@Valid @RequestBody ItemDto itemDto,
                          BindingResult br,
                          @RequestHeader(name = PARAM_NAME_OWNER_ID) Long ownerId) {
        log.debug("/create");
        return itemService.create(itemDto, ownerId, br);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader(name = PARAM_NAME_OWNER_ID) Long ownerId) {
        log.debug("/update");
        return itemService.update(itemId, itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Long itemId) {
        log.debug("/get");
        return itemService.get(itemId);
    }

    @GetMapping
    public List<ItemDto> getByOwner(@RequestHeader(name = PARAM_NAME_OWNER_ID) Long ownerId) {
        log.debug("/getByOwner");
        return itemService.getByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.debug("/search");
        return itemService.search(text);
    }
}