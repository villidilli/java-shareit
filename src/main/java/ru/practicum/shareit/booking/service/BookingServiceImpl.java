package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Transaction;
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
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.sql.Timestamp;
import java.util.Optional;

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

    @Override
    public BookingResponseDto create(BookingRequestDto bookingIncomeDto, BindingResult br, Long bookerId) {
        log.debug("/create");
        annotationValidate(br);
        customValidate(bookingIncomeDto);
        userService.getByIdOrThrow(bookerId);
        itemService.getByIdOrThrow(bookingIncomeDto.getItemId());
        itemService.checkAvailable(bookingIncomeDto.getItemId());

        log.debug("ДОСТАЛ НАПРЯМУЮ ИЗ ЮЗЕР СЕРВИСА ->>>>>" + userService.getByIdOrThrow(bookerId));
        log.debug("ДОСТАЛ НАПРЯМУЮ ИЗ ИТЕМ СЕРВИСА ->>>>>" + itemService.getByIdOrThrow(bookingIncomeDto.getItemId()));
        Booking savedBooking = bookingStorage.save(toBooking(bookingIncomeDto,bookerId));
        log.debug("СОХРАНЕННЫЙ В БД БУКИНГ ->>>>>" + savedBooking);
        log.debug("ДОСТАЛ ИЗ БУКИНГ СЕРВИСА ЧЕРЕЗ СУЩНОСТЬ БУКИНГ ->>>>> " + bookingStorage.getReferenceById(savedBooking.getId()));
        return null;
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