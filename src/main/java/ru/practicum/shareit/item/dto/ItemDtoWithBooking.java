package ru.practicum.shareit.item.dto;

import lombok.*;

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


    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class BookingShortDto {
        private Long id;
        private Long bookerId;
    }
}
