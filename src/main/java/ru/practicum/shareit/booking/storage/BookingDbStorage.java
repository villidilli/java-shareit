package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;

public interface BookingDbStorage extends JpaRepository<Booking, Long> {
}