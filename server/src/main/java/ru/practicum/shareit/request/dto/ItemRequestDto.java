package ru.practicum.shareit.request.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created = LocalDateTime.now();
}