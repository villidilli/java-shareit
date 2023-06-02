package ru.practicum.shareit.item.dto;

import lombok.*;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CommentDto {
    private Long id;
//    @NotBlank(message = "Text not be empty")
    private String text;
    private String authorName;
//    @DateTimeFormat(pattern = "YYYY-MM-DD HH:mm:ss")
    private LocalDateTime created = LocalDateTime.now();
}