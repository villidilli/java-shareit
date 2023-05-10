package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.BookingDtoMapper.toBooking;
import static ru.practicum.shareit.booking.dto.BookingDtoMapper.toBookingDto;
import static ru.practicum.shareit.exception.NotFoundException.*;
import static ru.practicum.shareit.exception.ValidateException.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final UserService userService;
    private final ItemService itemService;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    public BookingResponseDto create(BookingRequestDto bookingIncomeDto, BindingResult br, Long bookerId) {
        log.debug("/create");
        annotationValidate(br);
        customValidate(bookingIncomeDto);
        userService.isExist(bookerId);
        itemService.isExist(bookingIncomeDto.getItemId());
        itemService.checkAvailable(bookingIncomeDto.getItemId());
        User booker = userStorage.getReferenceById(bookerId);
        Item item = itemStorage.getReferenceById(bookingIncomeDto.getItemId());
        Booking savedBooking = bookingStorage.save(toBooking(bookingIncomeDto, booker, item));
        return toBookingDto(savedBooking);
    }

    @Override
    public BookingResponseDto update(Long bookingId, Long bookerId, Boolean status) {
        isExist(bookingId);
        Booking booking = bookingStorage.getReferenceById(bookingId);
        userService.isExist(bookerId);
        itemService.isOwnerOfItem(booking.getItem().getId(), bookerId);
        if (status) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return toBookingDto(bookingStorage.save(booking));
    }

    private void annotationValidate(BindingResult br) {
        log.debug("/annotationValidate");
        if (br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
    }

    private void customValidate(BookingRequestDto bookingIncomeDto) {
        log.debug("/customValidate");
        Timestamp startTime = Timestamp.valueOf(bookingIncomeDto.getStart());
        Timestamp endTime = Timestamp.valueOf(bookingIncomeDto.getEnd());
        if(endTime.before(startTime)
           || endTime.equals(startTime)) throw new ValidateException(ENDTIME_BEFORE_STARTTIME);
    }

    @Override
    @Transactional(readOnly = true)
    public void isExist(Long bookingId) {
        log.debug("/isExist");
        if(!bookingStorage.existsById(bookingId)) throw new NotFoundException(BOOKING_NOT_FOUND);
    }

    @Transactional(readOnly = true)
    public void isUserBookerOrOwner(Long userId, Long bookingId) {
        Booking booking = bookingStorage.getReferenceById(bookingId);
        if (booking.getBooker().getId() != userId
            && booking.getItem().getOwner().getId() != userId) {
            throw new ValidateException(USER_NOT_RELATED_FOR_BOOKING);
        }
    }

    @Override
    public BookingResponseDto getByUser(Long userId, Long bookingId) {
        log.debug("/getByUser");
        userService.isExist(userId);
        isExist(bookingId);
        isUserBookerOrOwner(userId, bookingId);
        return toBookingDto(bookingStorage.getReferenceById(bookingId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingResponseDto> getAllByBooker(Long bookerId, String state) {
        log.debug("/getAllByBooker");
        userService.isExist(bookerId);
        List<Booking> bookings;

        switch (BookingState.valueOf(state)) {
            case All:
                bookings = bookingStorage.findAllByBooker_IdOrderByIdDesc(bookerId);
            case CURRENT:
                bookings = bookingStorage.findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(
                        bookerId, LocalDateTime.now(), LocalDateTime.now()); break;
            case PAST:
                bookings = bookingStorage.findAllByBooker_idAndEndIsBefore(
                        bookerId, LocalDateTime.now()); break;
            case FUTURE:
                bookings = bookingStorage.findAllByBooker_idAndStartIsAfter(
                        bookerId, LocalDateTime.now()); break;
            case WAITING:
                bookings = bookingStorage.findAllByBooker_IdAndStatusIs(bookerId, BookingStatus.WAITING); break;
            case REJECTED:
                bookings = bookingStorage.findAllByBooker_IdAndStatusIs(bookerId, BookingStatus.REJECTED); break;
            default: return Collections.emptyList();
        }
        return bookings.stream().map(BookingDtoMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllByOwner(Long ownerId) {
        log.debug("/getAllByOwner");
        userService.isExist(ownerId);
        return bookingStorage.findAllByItem_Owner_IdOrderByIdDesc(ownerId).stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> TESTgetAll() {
        return bookingStorage.findAll().stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}