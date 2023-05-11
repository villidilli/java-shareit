package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.JoinColumn;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemDtoWithBooking {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentDto> comments;


    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class BookingShortDto {
        private Long id;
        private Long bookerId;
    }
}
