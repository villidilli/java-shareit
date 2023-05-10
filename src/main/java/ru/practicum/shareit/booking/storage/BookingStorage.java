package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItem_Owner_IdOrderByIdDesc(Long ownerId);

    List<Booking> findAllByBooker_IdOrderByIdDesc(Long bookerId);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(
            Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBooker_idAndEndIsBefore(
            Long bookerId, LocalDateTime end);

    List<Booking> findAllByBooker_idAndStartIsAfter(
            Long bookerId, LocalDateTime start);

    List<Booking> findAllByBooker_IdAndStatusIs(Long bookerId, BookingStatus status);
}