package ru.practicum.shareit.user.dto;

import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class UserDto {
    private Long id;
    private String name;
    @Email(message = "invalid email")
    @NotBlank(message = "Email must not be null or blank")
    private String email;
}