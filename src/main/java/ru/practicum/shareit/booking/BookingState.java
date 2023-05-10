package ru.practicum.shareit.booking;

public enum BookingState {
    All("все"),
    CURRENT("текущие"),
    PAST("завершенные"),
    FUTURE("будущие"),
    WAITING("ожидающие подтверждения"),
    REJECTED("отклоненные");

    String message;

    BookingState(String message){
        this.message = message;
    }
}