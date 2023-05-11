package ru.practicum.shareit.booking.model;

public enum BookingState {
    ALL("все"),
    CURRENT("текущие"),
    PAST("завершенные"),
    FUTURE("будущие"),
    WAITING("ожидающие подтверждения"),
    REJECTED("отклоненные");

    final String message;

    BookingState(String message) {
        this.message = message;
    }
}