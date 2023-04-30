package ru.practicum.shareit.user.dto;

import lombok.*;
import javax.validation.constraints.Email;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class UserDto {
    private Long id;
    private String name;
    @Email(message = "invalid email")
    private String email;
}