package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ItemDto {
    private Long id;
    @NotBlank(message = "Name must not be empty")
    private String name;
    @NotBlank(message = "Description must not be empty")
    private String description;
    @NotNull(message = "Available must not be null")
    private Boolean available;
}