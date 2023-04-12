package ru.practicum.shareit.item.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.NotFoundException.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    public Item create(Item item, BindingResult br) {
        log.debug("/create");
        annotationValidate(br);
        userService.isExist(item.getOwner());
        return itemStorage.add(item);
    }

    @SneakyThrows
    @Override
    public Item update(Long itemId, Item itemFromDto) {
        log.debug("/update");
        isExist(itemId);
        checkItemOwner(itemId, itemFromDto.getOwner());
        Item savedItem = get(itemId);
        userService.isExist(itemFromDto.getOwner());
        Map<String, String> itemMap = objectMapper.convertValue(itemFromDto, Map.class);
        Map<String, String> valuesToUpdate = itemMap.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        objectMapper.updateValue(savedItem, valuesToUpdate);
        return itemStorage.update(itemId, savedItem);
    }

    @Override
    public Item get(Long itemId) {
        log.debug("/get");
        return itemStorage.get(itemId);
    }

    @Override
    public List<Item> getByOwner(Long ownerId) {
        log.debug("/getByOwner");
        userService.isExist(ownerId);
        return itemStorage.getAll().stream()
                .filter(item -> Objects.equals(item.getOwner(), ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        log.debug("/search");
        if (text.isBlank()) return Collections.emptyList();
        return itemStorage.getAll().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())) && item.getAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public void checkItemOwner(Long itemId, Long ownerId) throws NotFoundException {
        log.debug("/checkItemOwner");
        if (!Objects.equals(itemStorage.get(itemId).getOwner(), ownerId)) {
            throw new NotFoundException(OWNER_NOT_MATCH_ITEM);
        }
    }

    @Override
    public void annotationValidate(BindingResult br) {
        log.debug("/annotationValidate");
        if (br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
    }

    @Override
    public void isExist(Long itemId) {
        log.debug("/isExist");
        if (itemStorage.get(itemId) == null) throw new NotFoundException(ITEM_NOT_FOUND);
    }
}