package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ItemResponseDto {
    private Long id;
    private String description;
    private LocalDateTime created = LocalDateTime.now();
    private List<ItemShortDto> items;

    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class ItemShortDto {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long requestId;
    }
}