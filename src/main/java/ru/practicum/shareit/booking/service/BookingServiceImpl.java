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
import ru.practicum.shareit.user.UserRole;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static ru.practicum.shareit.booking.BookingStatus.*;
import static ru.practicum.shareit.booking.dto.BookingDtoMapper.toBooking;
import static ru.practicum.shareit.booking.dto.BookingDtoMapper.toBookingDto;
import static ru.practicum.shareit.exception.NotFoundException.*;
import static ru.practicum.shareit.exception.ValidateException.*;
import static ru.practicum.shareit.user.UserRole.BOOKER;

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
        isBookerIsOwnerItem(bookingIncomeDto.getItemId(), bookerId);
        User booker = userStorage.getReferenceById(bookerId);
        Item item = itemStorage.getReferenceById(bookingIncomeDto.getItemId());
        Booking savedBooking = bookingStorage.save(toBooking(bookingIncomeDto, booker, item));
        return toBookingDto(savedBooking);
    }

    private void isBookerIsOwnerItem(Long itemId, Long bookerId) {
        Long itemOwnerId = itemStorage.getReferenceById(itemId).getOwner().getId();
        if(bookerId.equals(itemOwnerId)) throw new NotFoundException(BOOKER_IS_OWNER_ITEM);
    }

    @Override
    public BookingResponseDto update(Long bookingId, Long ownerId, String status) {
        isExist(bookingId);
        userService.isExist(ownerId);
        Booking booking = bookingStorage.getReferenceById(bookingId);
        itemService.isOwnerOfItem(booking.getItem().getId(), ownerId);
        isStatusWaiting(booking);
        if(status != null) {
            if(Boolean.parseBoolean(status)) booking.setStatus(APPROVED);
            if(!Boolean.parseBoolean(status)) booking.setStatus(REJECTED);
        }
        return toBookingDto(bookingStorage.save(booking));
    }

    private void isStatusWaiting(Booking booking) {
        BookingStatus status = booking.getStatus();
        if(status != WAITING) throw new ValidateException(STATUS_NOT_WAITING);
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
            throw new NotFoundException(USER_NOT_RELATED_FOR_BOOKING);
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
    public List<BookingResponseDto> getAllByUser(Long userId, String state, UserRole userRole) {
        log.debug("/getAllByUser");
        userService.isExist(userId);
        List<Booking> bookings = Collections.emptyList();
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidateException(STATE_INCORRECT_INPUT + state);
        }
        final LocalDateTime curTime = LocalDateTime.now();
        switch (bookingState) {
            case ALL:
                log.debug("switch state - ALL");
                if(userRole == BOOKER) {
                    log.debug("switch role - BOOKER");
                    bookings = bookingStorage.findAllByBooker_IdOrderByIdDesc(userId); break;
                }
                log.debug("switch role - OWNER");
                bookings = bookingStorage.findAllByItem_Owner_IdOrderByIdDesc(userId); break;
            case CURRENT:
                log.debug("switch state - CURRENT");
                if(userRole == BOOKER) {
                    log.debug("switch role - BOOKER");
                    bookings = bookingStorage.findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByIdDesc(
                            userId, curTime, curTime); break;
                }
                log.debug("switch role - OWNER");
                bookings = bookingStorage.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByIdDesc(
                            userId, curTime, curTime); break;
            case PAST:
                log.debug("switch state - PAST");
                if(userRole == BOOKER) {
                    log.debug("switch role - BOOKER");
                    bookings = bookingStorage.findAllByBooker_idAndEndIsBeforeOrderByIdDesc(userId, curTime); break;
                }
                log.debug("switch role - OWNER");
                bookings = bookingStorage.findAllByItem_Owner_IdAndEndIsBeforeOrderByIdDesc(userId, curTime); break;
            case FUTURE:
                log.debug("switch state - FUTURE");
                if(userRole == BOOKER) {
                    log.debug("switch role - BOOKER");
                    bookings = bookingStorage.findAllByBooker_idAndStartIsAfterOrderByIdDesc(userId, curTime); break;
                }
                log.debug("switch role - OWNER");
                bookings = bookingStorage.findAllByItem_Owner_IdAndStartIsAfterOrderByIdDesc(userId, curTime); break;
            case WAITING:
                log.debug("switch status - WAITING");
                if(userRole == BOOKER) {
                    log.debug("switch role - BOOKER");
                    bookings = bookingStorage.findAllByBooker_IdAndStatusIsOrderByIdDesc(userId, WAITING); break;
                }
                log.debug("switch role - OWNER");
                bookings = bookingStorage.findAllByItem_Owner_IdAndStatusIsOrderByIdDesc(userId, WAITING); break;
            case REJECTED:
                log.debug("switch status - REJECTED");
                if(userRole == BOOKER) {
                    log.debug("switch role - BOOKER");
                    bookings = bookingStorage.findAllByBooker_IdAndStatusIsOrderByIdDesc(userId, REJECTED); break;
                }
                log.debug("switch role - OWNER");
                bookings = bookingStorage.findAllByItem_Owner_IdAndStatusIsOrderByIdDesc(userId, REJECTED); break;
        }
        return bookings.stream().map(BookingDtoMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> TESTgetAll() {
        return bookingStorage.findAll().stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}