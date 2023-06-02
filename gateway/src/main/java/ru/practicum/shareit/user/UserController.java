package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserClient client;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto, BindingResult br) {
        log.debug("[GATEWAY]/create");
        return client.create(userDto, br);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.debug("[GATEWAY]/update");
        return client.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable Long userId) {
        log.debug("[GATEWAY]/get");
        return client.get(userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.debug("[GATEWAY]/delete");
        client.delete(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.debug("[GATEWAY]/getAll");
        return client.getAll();
    }
}