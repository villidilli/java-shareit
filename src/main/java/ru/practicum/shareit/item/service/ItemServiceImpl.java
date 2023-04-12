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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.NotFoundException.ITEM_NOT_FOUND;
import static ru.practicum.shareit.exception.NotFoundException.USER_NOT_FOUND;

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
        log.warn("ПРИШЕЛ BR в CREATE " + br.hasErrors());
        annotationValidate(br);
        userService.get(item.getOwner());
        return itemStorage.add(item);
    }

    @Override
    public void annotationValidate(BindingResult br) {
        log.debug("/annotationValidate");
        log.warn("ПРИШЕЛ BR в ANN VAL " + br.hasErrors());
        log.warn("ЕСТЬ ОШИБКИ" + br.getFieldErrors());
        if(br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
    }

    @SneakyThrows
    @Override
    public Item update(Long itemId, Item itemFromDto) {
        log.debug("/update");
        Item savedItem = get(itemId);
        userService.get(itemFromDto.getOwner());
        Map<String, String> itemMap = objectMapper.convertValue(itemFromDto, Map.class);
        Map<String, String> valuesToUpdate = itemMap.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        objectMapper.updateValue(savedItem, valuesToUpdate);
        return itemStorage.update(itemId, savedItem);

    }

    @Override
    public Item get(Long itemId) {
        Item returnedItem = itemStorage.get(itemId);
        if(returnedItem == null) throw new NotFoundException(ITEM_NOT_FOUND);
        return returnedItem;
    }
}
