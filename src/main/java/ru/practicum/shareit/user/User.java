package ru.practicum.shareit.user;

import lombok.*;

/**
 * TODO Sprint add-controllers.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {
    private Long id;
    private String name;
    private String email;
}