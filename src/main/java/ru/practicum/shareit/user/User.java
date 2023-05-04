package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@NoArgsConstructor
@Getter @Setter @ToString
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "email", nullable = false)
    private String email;
}