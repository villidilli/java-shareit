package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItem_Owner_IdOrderByIdDesc(Long ownerId);

    List<Booking> findAllByBooker_IdOrderByIdDesc(Long bookerId);

    List<Booking> findAllByBooker_idAndEndIsBeforeOrderByIdDesc(Long bookerId, LocalDateTime end);

    List<Booking> findAllByItem_Owner_IdAndEndIsBeforeOrderByIdDesc(Long ownerId, LocalDateTime end);

    List<Booking> findAllByBooker_idAndStartIsAfterOrderByIdDesc(Long bookerId, LocalDateTime start);

    List<Booking> findAllByItem_Owner_IdAndStartIsAfterOrderByIdDesc(Long ownerId, LocalDateTime start);

    List<Booking> findAllByBooker_IdAndStatusIsOrderByIdDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByItem_Owner_IdAndStatusIsOrderByIdDesc(Long ownerId, BookingStatus status);

    Long countBookingsByBooker_IdAndItem_IdAndEndBefore(Long bookerId, Long itemId, LocalDateTime end);


    Booking findTopByItem_IdAndStatusIsNotAndStartIsBeforeOrderByEndDesc(
                                                            Long item_id, BookingStatus status, LocalDateTime start);

    Booking findTopByItem_IdAndStatusIsNotAndStartIsAfterOrderByStartAsc(
                                                            Long item_id, BookingStatus status, LocalDateTime start);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByIdAsc(
            Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByIdAsc(
            Long ownerId, LocalDateTime start, LocalDateTime end);
}