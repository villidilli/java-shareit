package ru.practicum.shareit.item.service;

import org.springframework.validation.BindingResult;
import ru.practicum.shareit.item.Item;

public interface ItemService {

    Item create(Item item, BindingResult br);

    public void annotationValidate(BindingResult br);

    Item update(Long itemId, Item item);

    Item get(Long itemId);
}
