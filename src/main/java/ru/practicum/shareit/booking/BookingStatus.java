package ru.practicum.shareit.booking;

public enum BookingStatus {
    WAITING("ожидает одобрения владельца"),
    APPROVED("подтверждено владельцем"),
    REJECTED("отклонено владельцем"),
    CANCELED("отменено создателем");

    public final String message;

    private BookingStatus(String message) {
        this.message = message;
    }
}