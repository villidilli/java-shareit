package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByItem_Owner_Id(Long ownerId, Pageable page);

    Page<Booking> findAllByBooker_Id(Long bookerId, Pageable page);

    Page<Booking> findAllByBooker_idAndEndIsBefore(Long bookerId, LocalDateTime end, Pageable page);

    Page<Booking> findAllByItem_Owner_IdAndEndIsBefore(Long ownerId, LocalDateTime end, Pageable page);

    Page<Booking> findAllByBooker_idAndStartIsAfter(Long bookerId, LocalDateTime start, Pageable page);

    Page<Booking> findAllByItem_Owner_IdAndStartIsAfter(Long ownerId, LocalDateTime start, Pageable page);

    Page<Booking> findAllByBooker_IdAndStatusIs(Long bookerId, BookingStatus status, Pageable page);

    Page<Booking> findAllByItem_Owner_IdAndStatusIs(Long ownerId, BookingStatus status, Pageable page);

    Long countBookingsByBooker_IdAndItem_IdAndEndBefore(Long bookerId, Long itemId, LocalDateTime end);

    Page<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(
                                    Long bookerId, LocalDateTime start, LocalDateTime end, Pageable page);

    Page<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
                                    Long ownerId, LocalDateTime start, LocalDateTime end, Pageable page);

    List<Booking> findByItem_Owner_Id(Long ownerId);

    List<Booking> findByItem_Owner_IdAndItem_Id(Long ownerId, Long itemId);
}