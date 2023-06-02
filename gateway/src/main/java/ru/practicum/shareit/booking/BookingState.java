package ru.practicum.shareit.booking;

public enum BookingState {
    ALL("все"),
    CURRENT("текущие"),
    PAST("завершенные"),
    FUTURE("будущие"),
    WAITING("ожидающие подтверждения"),
    REJECTED("отклоненные");

    private final String message;

    BookingState(String message) {
        this.message = message;
    }
}