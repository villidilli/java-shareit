package ru.practicum.shareit.booking.model;

public enum BookingStatus {
    WAITING("ожидает одобрения владельца"),
    APPROVED("подтверждено владельцем"),
    REJECTED("отклонено владельцем"),
    CANCELED("отменено создателем");

    private final String message;

    private BookingStatus(String message) {
        this.message = message;
    }
}