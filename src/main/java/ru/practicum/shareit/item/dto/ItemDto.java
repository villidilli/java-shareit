package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */

@AllArgsConstructor
@Getter
@Setter
public class ItemDto {
    private String name;
    private String description;
    private boolean isAvailable;
    private Long request;
}