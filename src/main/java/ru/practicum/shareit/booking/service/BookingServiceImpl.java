package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
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
import static ru.practicum.shareit.request.controller.ItemRequestController.DEFAULT_FIRST_PAGE;

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
    private final Sort sortByIdDesc = Sort.by("id").descending();
    private final Sort sortByIdAsc = Sort.by("id").ascending();

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
        Booking savedBooking = bookingStorage.save(toBooking(bookingIncomeDto, booker, item, WAITING));
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
            boolean statusBool = Boolean.parseBoolean(status);
            if (statusBool) booking.setStatus(APPROVED);
            if (!statusBool) booking.setStatus(REJECTED);
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
    public List<BookingResponseDto> getAllByBooker(Long userId, String state, Integer from, Integer size)
                                                                                            throws NotFoundException {
        log.debug("/getAllByUser");
        userService.isExist(userId);
        final LocalDateTime curTime = LocalDateTime.now();
        Page<Booking> bookings = Page.empty();
        BookingState bookingState = toBookingState(state);
        Pageable pageSortDesc = getPage(from, size, sortByIdDesc);
        Pageable pageSortAsc = getPage(from, size, sortByIdAsc);

        switch (bookingState) {
            case ALL:
                log.debug("switch state - ALL");
                bookings = bookingStorage.findAllByBooker_Id(userId, pageSortDesc);
                break;
            case CURRENT:
                log.debug("switch state - CURRENT");
                bookings = bookingStorage.findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(
                                                                        userId, curTime, curTime, pageSortAsc);
                break;
            case PAST:
                log.debug("switch state - PAST");
                bookings = bookingStorage.findAllByBooker_idAndEndIsBefore(userId, curTime, pageSortDesc);
                break;
            case FUTURE:
                log.debug("switch state - FUTURE");
                bookings = bookingStorage.findAllByBooker_idAndStartIsAfter(userId, curTime, pageSortDesc);
                break;
            case WAITING:
                log.debug("switch status - WAITING");
                bookings = bookingStorage.findAllByBooker_IdAndStatusIs(userId, WAITING, pageSortDesc);
                break;
            case REJECTED:
                log.debug("switch status - REJECTED");
                bookings = bookingStorage.findAllByBooker_IdAndStatusIs(userId, REJECTED, pageSortDesc);
                break;
        }
        return bookings.stream().map(BookingDtoMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllByOwner(Long userId, String state, Integer from, Integer size)
                                                                                            throws NotFoundException {
        log.debug("/getAllByOwner");
        userService.isExist(userId);
        final LocalDateTime curTime = LocalDateTime.now();
        Page<Booking> bookings = Page.empty();
        BookingState bookingState = toBookingState(state);
        Pageable pageSortDesc = getPage(from, size, sortByIdDesc);
        Pageable pageSortAsc = getPage(from, size, sortByIdAsc);

        switch (bookingState) {
            case ALL:
                log.debug("switch state - ALL");
                bookings = bookingStorage.findAllByItem_Owner_Id(userId, pageSortDesc);
                break;
            case CURRENT:
                log.debug("switch state - CURRENT");
                bookings = bookingStorage.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
                                                                    userId, curTime, curTime, pageSortAsc);
                break;
            case PAST:
                log.debug("switch state - PAST");
                bookings = bookingStorage.findAllByItem_Owner_IdAndEndIsBefore(userId, curTime, pageSortDesc);
                break;
            case FUTURE:
                log.debug("switch state - FUTURE");
                bookings = bookingStorage.findAllByItem_Owner_IdAndStartIsAfter(userId, curTime, pageSortDesc);
                break;
            case WAITING:
                log.debug("switch status - WAITING");
                bookings = bookingStorage.findAllByItem_Owner_IdAndStatusIs(userId, WAITING, pageSortDesc);
                break;
            case REJECTED:
                log.debug("switch status - REJECTED");
                bookings = bookingStorage.findAllByItem_Owner_IdAndStatusIs(userId, REJECTED, pageSortDesc);
                break;
        }
        return bookings.stream().map(BookingDtoMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public void isExist(Long bookingId) throws NotFoundException {
        log.debug("/isExist");
        if (!bookingStorage.existsById(bookingId)) throw new NotFoundException(BOOKING_NOT_FOUND);
    }

    private Pageable getPage(Integer from, Integer size, Sort sort) {
        int firstPage = from != 0 ? from / size : Integer.parseInt(DEFAULT_FIRST_PAGE);
        return PageRequest.of(firstPage, size, sort);
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