package ru.practicum.shareit.item.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.NotFoundException.*;
import static ru.practicum.shareit.item.dto.ItemDtoMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId, BindingResult br) {
        log.debug("/create");
        annotationValidate(br);
        Item item = toItem(itemDto, ownerId);
        userService.getById(ownerId);
        log.warn(item.toString());
        return toItemDto(itemStorage.save(item));
    }

    @SneakyThrows
    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long ownerId) {
        log.debug("/update");
        isOwnerOfItem(itemId, ownerId);
        Item existedItem = getById(itemId);
        Item itemWithUpdate = toItem(itemDto, ownerId);
        Item updatedItem = setNewFields(existedItem, itemWithUpdate);
        return toItemDto(itemStorage.save(updatedItem));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto get(Long itemId) {
        log.debug("/get");
        return toItemDto(getById(itemId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getByOwner(Long ownerId) {
        log.debug("/getByOwner");
        userService.getById(ownerId);
        return itemStorage.findByOwnerId(ownerId).stream().map(ItemDtoMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text) {
        log.debug("/search");
        if (text.isBlank()) return Collections.emptyList();
        return itemStorage.findByAvailableIsTrueAndNameContainsIgnoreCaseOrDescriptionContainsIgnoreCase(text, text)
                .stream()
//                .filter(item -> item.getAvailable()
//                        && (item.getName().toLowerCase().contains(text.toLowerCase())
//                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
//                )
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Item getById(Long itemId) {
        log.debug("/getById");
        return itemStorage.findById(itemId).orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND));
    }

    private Item setNewFields(Item existedItem, Item itemWithUpdate) {
        log.debug("/setNewFields");
        Map<String, String> fieldsToUpdate = getFieldToUpdate(itemWithUpdate);
        Map<String, String> existedItemMap = objectMapper.convertValue(existedItem, Map.class);
        existedItemMap.putAll(fieldsToUpdate);
        return objectMapper.convertValue(existedItemMap, Item.class);
    }

    private void isOwnerOfItem(Long itemId, Long ownerId) throws NotFoundException {
        log.debug("/isOwnerOfItem");
        if (!Objects.equals(getById(itemId).getOwner().getId(), ownerId)) {
            throw new NotFoundException(OWNER_NOT_MATCH_ITEM);
        }
    }

    private void annotationValidate(BindingResult br) {
        log.debug("/annotationValidate");
        if (br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
    }

    private Map<String, String> getFieldToUpdate(Item itemWithUpdate) {
        Map<String, String> mapWithNullFields = objectMapper.convertValue(itemWithUpdate, Map.class);
        return mapWithNullFields.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}