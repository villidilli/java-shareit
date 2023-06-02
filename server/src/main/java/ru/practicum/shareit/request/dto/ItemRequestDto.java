package ru.practicum.shareit.request.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ItemRequestDto {
    private Long id;
//    @NotBlank(message = "Description must not be blank or empty")
    private String description;
//    @DateTimeFormat(pattern = "YYYY-MM-DD HH:mm:ss")
    private LocalDateTime created = LocalDateTime.now();
}