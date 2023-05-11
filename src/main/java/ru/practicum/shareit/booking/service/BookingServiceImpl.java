package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.validation.BindingResult;

import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.storage.BookingStorage;

import ru.practicum.shareit.exception.*;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemStorage;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserRole;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.*;
import static ru.practicum.shareit.booking.dto.BookingDtoMapper.toBooking;
import static ru.practicum.shareit.booking.dto.BookingDtoMapper.toBookingDto;
import static ru.practicum.shareit.exception.NotFoundException.*;
import static ru.practicum.shareit.exception.ValidateException.*;
import static ru.practicum.shareit.user.model.UserRole.BOOKER;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final UserService userService;
    private final ItemService itemService;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Transactional
    @Override
    public BookingResponseDto create(BookingRequestDto bookingIncomeDto, BindingResult br, Long bookerId)
                                                            throws ValidateException, NotFoundException {
        log.debug("/create");
        annotationValidate(br);
        customValidate(bookingIncomeDto);
        userService.isExist(bookerId);
        itemService.isExist(bookingIncomeDto.getItemId());
        itemService.isItemAvailable(bookingIncomeDto.getItemId());
        isBookerIsOwner(bookingIncomeDto.getItemId(), bookerId);
        User booker = userStorage.getReferenceById(bookerId);
        Item item = itemStorage.getReferenceById(bookingIncomeDto.getItemId());
        Booking savedBooking = bookingStorage.save(toBooking(bookingIncomeDto, booker, item));
        return toBookingDto(savedBooking);
    }

    @Transactional
    @Override
    public BookingResponseDto update(Long bookingId, Long ownerId, String status) throws NotFoundException {
        log.debug("/update");
        isExist(bookingId);
        userService.isExist(ownerId);
        Booking booking = bookingStorage.getReferenceById(bookingId);
        itemService.isOwnerOfItem(booking.getItem().getId(), ownerId);
        isStatusIsWaiting(booking);
        if (status != null) {
            if (Boolean.parseBoolean(status)) booking.setStatus(APPROVED);
            if (!Boolean.parseBoolean(status)) booking.setStatus(REJECTED);
        }
        return toBookingDto(bookingStorage.save(booking));
    }

    @Override
    public BookingResponseDto getByUser(Long userId, Long bookingId) throws NotFoundException {
        log.debug("/getByUser");
        userService.isExist(userId);
        isExist(bookingId);
        isUserBookerOrOwner(userId, bookingId);
        return toBookingDto(bookingStorage.getReferenceById(bookingId));
    }

    @Override
    public List<BookingResponseDto> getAllByUser(Long userId, String state, UserRole role) throws NotFoundException {
        log.debug("/getAllByUser");
        userService.isExist(userId);
        final LocalDateTime curTime = LocalDateTime.now();
        List<Booking> bookings = Collections.emptyList();
        BookingState bookingState = toBookingState(state);

        switch (bookingState) {
            case ALL:
                log.debug("switch state - ALL");
                if (role == BOOKER) {
                    log.debug("switch role - BOOKER");
                    bookings = bookingStorage.findAllByBooker_IdOrderByIdDesc(userId); break;
                }
                log.debug("switch role - OWNER");
                bookings = bookingStorage.findAllByItem_Owner_IdOrderByIdDesc(userId); break;
            case CURRENT:
                log.debug("switch state - CURRENT");
                if (role == BOOKER) {
                    log.debug("switch role - BOOKER");
                    bookings = bookingStorage.findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByIdAsc(
                            userId, curTime, curTime); break;
                }
                log.debug("switch role - OWNER");
                bookings = bookingStorage.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByIdAsc(
                            userId, curTime, curTime); break;
            case PAST:
                log.debug("switch state - PAST");
                if (role == BOOKER) {
                    log.debug("switch role - BOOKER");
                    bookings = bookingStorage.findAllByBooker_idAndEndIsBeforeOrderByIdDesc(userId, curTime); break;
                }
                log.debug("switch role - OWNER");
                bookings = bookingStorage.findAllByItem_Owner_IdAndEndIsBeforeOrderByIdDesc(userId, curTime); break;
            case FUTURE:
                log.debug("switch state - FUTURE");
                if (role == BOOKER) {
                    log.debug("switch role - BOOKER");
                    bookings = bookingStorage.findAllByBooker_idAndStartIsAfterOrderByIdDesc(userId, curTime); break;
                }
                log.debug("switch role - OWNER");
                bookings = bookingStorage.findAllByItem_Owner_IdAndStartIsAfterOrderByIdDesc(userId, curTime); break;
            case WAITING:
                log.debug("switch status - WAITING");
                if (role == BOOKER) {
                    log.debug("switch role - BOOKER");
                    bookings = bookingStorage.findAllByBooker_IdAndStatusIsOrderByIdDesc(userId, WAITING); break;
                }
                log.debug("switch role - OWNER");
                bookings = bookingStorage.findAllByItem_Owner_IdAndStatusIsOrderByIdDesc(userId, WAITING); break;
            case REJECTED:
                log.debug("switch status - REJECTED");
                if (role == BOOKER) {
                    log.debug("switch role - BOOKER");
                    bookings = bookingStorage.findAllByBooker_IdAndStatusIsOrderByIdDesc(userId, REJECTED); break;
                }
                log.debug("switch role - OWNER");
                bookings = bookingStorage.findAllByItem_Owner_IdAndStatusIsOrderByIdDesc(userId, REJECTED); break;
        }
        return bookings.stream().map(BookingDtoMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public void isExist(Long bookingId) throws NotFoundException {
        log.debug("/isExist");
        if (!bookingStorage.existsById(bookingId)) throw new NotFoundException(BOOKING_NOT_FOUND);
    }

    private void annotationValidate(BindingResult br) throws ValidateException {
        log.debug("/annotationValidate");
        if (br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
    }

    private void customValidate(BookingRequestDto bookingIncomeDto) throws ValidateException {
        log.debug("/customValidate");
        Timestamp startTime = Timestamp.valueOf(bookingIncomeDto.getStart());
        Timestamp endTime = Timestamp.valueOf(bookingIncomeDto.getEnd());
        if (endTime.before(startTime)
                || endTime.equals(startTime)) throw new ValidateException(ENDTIME_BEFORE_STARTTIME);
    }

    private void isBookerIsOwner(Long itemId, Long bookerId) throws NotFoundException {
        log.debug("/isBookerIsOwner");
        Long itemOwnerId = itemStorage.getReferenceById(itemId).getOwner().getId();
        if (bookerId.equals(itemOwnerId)) throw new NotFoundException(BOOKER_IS_OWNER_ITEM);
    }

    private void isStatusIsWaiting(Booking booking) throws ValidateException {
        log.debug("/isStatusIsWaiting");
        BookingStatus status = booking.getStatus();
        if (status != WAITING) throw new ValidateException(STATUS_NOT_WAITING);
    }

    private void isUserBookerOrOwner(Long userId, Long bookingId) throws NotFoundException {
        log.debug("/isUserBookerOrOwner");
        Booking booking = bookingStorage.getReferenceById(bookingId);
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException(USER_NOT_RELATED_FOR_BOOKING);
        }
    }

    private BookingState toBookingState(String state) throws ValidateException {
        log.debug("/toBookingState");
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidateException(STATE_INCORRECT_INPUT + state);
        }
    }
}