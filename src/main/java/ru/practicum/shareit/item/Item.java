package ru.practicum.shareit.item;

import lombok.*;

/**
 * TODO Sprint add-controllers.
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Item {
    private Long id;
    private Long owner;
    private String name;
    private String description;
    private Boolean available;
    private Long request;
}