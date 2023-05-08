package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.storage.BookingDbStorage;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.sql.Timestamp;

import static ru.practicum.shareit.booking.dto.BookingDtoMapper.toBooking;
import static ru.practicum.shareit.booking.dto.BookingDtoMapper.toBookingDto;
import static ru.practicum.shareit.exception.ValidateException.ENDTIME_BEFORE_STARTTIME;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class BookingServiceImpl implements BookingService {
    private final BookingDbStorage bookingStorage;
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
        log.debug("РЕФЕРЕНС НА ЮЗЕРА ---------- " + booker);
        Item item = itemStorage.getReferenceById(bookingIncomeDto.getItemId());
        log.debug("РЕФЕРЕНС НА ИТЕМ ---------- " + item);
        Booking savedBooking = bookingStorage.save(toBooking(bookingIncomeDto, item, booker));
        log.debug("SAVED ---------- "  + savedBooking);
        BookingResponseDto dto =  toBookingDto(savedBooking);
        log.debug("ИЗ СЕРВИСА ДТО === " + dto);
        return dto;
    }

    private void annotationValidate(BindingResult br) {
        log.debug("/annotationValidate");
        if (br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
    }

    private void customValidate(BookingRequestDto bookingIncomeDto) {
        log.debug("/customValidate");
        Timestamp startTime = Timestamp.valueOf(bookingIncomeDto.getStart());
        Timestamp endTime = Timestamp.valueOf(bookingIncomeDto.getEnd());
        if(endTime.before(startTime) || endTime.equals(startTime)) throw new ValidateException(ENDTIME_BEFORE_STARTTIME);
    }
}