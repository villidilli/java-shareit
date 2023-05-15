package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItem_Owner_Id(Long ownerId, Sort sort);

    List<Booking> findAllByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findAllByBooker_idAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndEndIsBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByBooker_idAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStartIsAfter(Long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByBooker_IdAndStatusIs(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStatusIs(Long ownerId, BookingStatus status, Sort sort);

    Long countBookingsByBooker_IdAndItem_IdAndEndBefore(Long bookerId, Long itemId, LocalDateTime end);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(
                                                    Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
                                                    Long ownerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByItem_Owner_Id(Long ownerId);

    List<Booking> findByItem_Owner_IdAndItem_Id(Long ownerId, Long itemId);
}