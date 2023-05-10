package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItem_Owner_IdOrderByIdDesc(Long ownerId);

    List<Booking> findAllByBooker_IdOrderByIdDesc(Long bookerId);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByIdDesc(
                                                            Long bookerId, LocalDateTime start, LocalDateTime end);
    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByIdDesc(
                                                            Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBooker_idAndEndIsBeforeOrderByIdDesc(Long bookerId, LocalDateTime end);

    List<Booking> findAllByItem_Owner_IdAndEndIsBeforeOrderByIdDesc(Long ownerId, LocalDateTime end);

    List<Booking> findAllByBooker_idAndStartIsAfterOrderByIdDesc(Long bookerId, LocalDateTime start);

    List<Booking> findAllByItem_Owner_IdAndStartIsAfterOrderByIdDesc(Long ownerId, LocalDateTime start);

    List<Booking> findAllByBooker_IdAndStatusIsOrderByIdDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByItem_Owner_IdAndStatusIsOrderByIdDesc(Long ownerId, BookingStatus status);

    List<Booking> findAllByItem_OwnerIdOrderByIdDesc(Long ownerId);

    Booking findTopByItem_IdAndStartIsBeforeOrderByEndDesc(Long item_id, LocalDateTime start);
    Booking findTopByItem_IdAndStartIsAfterOrderByStartAsc(Long item_id, LocalDateTime start);
}