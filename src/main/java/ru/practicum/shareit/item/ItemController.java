package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

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
        Item createdItem = itemService.create(ItemDtoMapper.toItem(itemDto, ownerId), br);
        return ItemDtoMapper.toItemDto(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader(name = PARAM_NAME_OWNER_ID) Long ownerId) {
        log.debug("/update");
        Item updItem = itemService.update(itemId, ItemDtoMapper.toItem(itemDto, ownerId));
        return ItemDtoMapper.toItemDto(updItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Long itemId) {
        log.debug("/get");
        return ItemDtoMapper.toItemDto(itemService.get(itemId));
    }

    @GetMapping
    public List<ItemDto> getByOwner(@RequestHeader(name = PARAM_NAME_OWNER_ID) Long ownerId) {
        log.debug("/getByOwner");
        return itemService.getByOwner(ownerId).stream().map(ItemDtoMapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.debug("/search");
        return itemService.search(text).stream().map(ItemDtoMapper::toItemDto).collect(Collectors.toList());
    }
}