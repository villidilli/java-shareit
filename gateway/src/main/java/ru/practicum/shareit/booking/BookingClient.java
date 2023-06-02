package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.exception.ValidateException;

import java.sql.Timestamp;
import java.util.Map;

import static ru.practicum.shareit.exception.ValidateException.ENDTIME_BEFORE_STARTTIME;
import static ru.practicum.shareit.exception.ValidateException.STATE_INCORRECT_INPUT;

@Service
@Slf4j
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";
    private static Map<String, Object> parameters;

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(long userId, BookingRequestDto requestDto, BindingResult br) {
        log.debug("[GATEWAY]/create");
        annotationValidate(br);
        customValidate(requestDto);
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> update(long ownerId, long bookingId, String status) {
        log.debug("[GATEWAY]/update");
//        parameters = Map.of("status", status);
        return patch("/" + bookingId + "?approved=" + status, ownerId); //parameters
    }

    public ResponseEntity<Object> getByUser(long userId, long bookingId) {
        log.debug("[GATEWAY]/getByUser");
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllByBooker(long userId, String state, int from, int size) {
        log.debug("[GATEWAY]/getAllByBooker");
        toBookingState(state);
        parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAllByOwner(long userId, String state, int from, int size) {
        log.debug("[GATEWAY]/getAllByOwner");
        toBookingState(state);
        parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    private void customValidate(BookingRequestDto bookingIncomeDto) throws ValidateException {
        log.debug("[GATEWAY]/customValidate");
        Timestamp startTime = Timestamp.valueOf(bookingIncomeDto.getStart());
        Timestamp endTime = Timestamp.valueOf(bookingIncomeDto.getEnd());
        if (endTime.before(startTime) || endTime.equals(startTime)) {
            throw new ValidateException(ENDTIME_BEFORE_STARTTIME);
        }
    }

    private void annotationValidate(BindingResult br) throws ValidateException {
        log.debug("[GATEWAY]/annotationValidate");
//        if (br.hasErrors()) {
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body(new ValidateException(GlobalExceptionHandler.bindingResultToString(br)));
//        }
        if (br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
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