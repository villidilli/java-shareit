package ru.practicum.shareit.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @Positive(message = "id must be greater 0")
    @EqualsAndHashCode.Include
    private Long id;
    @NotBlank(message = "name must not be blank")
    private String name;
    @NotBlank(message = "email must not be blank")
    @Email(message = "invalid email")
    private String email;
}