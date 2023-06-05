package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.Constant.*;


@Controller
@RequestMapping(path = "/requests")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient client;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestDto requestDto,
                                         BindingResult br,
                                         @RequestHeader(PARAM_USER_ID) Long userId) {
        log.debug("[GATEWAY]/create");
        return client.create(requestDto, br, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwn(@RequestHeader(PARAM_USER_ID) Long requesterId) {
        log.debug("[GATEWAY]/getAllOwn");
        return client.getAllOwn(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllNotOwn(
            @RequestHeader(PARAM_USER_ID) Long requesterId,
            @RequestParam(value = FIRST_PAGE, defaultValue = DEFAULT_FIRST_PAGE) @PositiveOrZero Integer from,
            @RequestParam(value = SIZE_VIEW, defaultValue = DEFAULT_SIZE_VIEW) @Positive Integer size) {
        log.debug("[GATEWAY]getAllNotOwn");
        return client.getAllNotOwn(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(PARAM_USER_ID) Long requesterId,
                                      @PathVariable Long requestId) {
        log.debug("[GATEWAY]/getById");
        return client.getById(requesterId, requestId);
    }
}