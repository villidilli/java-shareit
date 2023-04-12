package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserDto userDto, BindingResult br) {
        log.debug("/create");
        User user = UserDtoMapper.toUser(userDto);
        User createdUser = userService.create(user, br);
        return UserDtoMapper.toUserDto(createdUser);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.debug("/update");
        log.debug("Так смапился USER DTO " + userDto.toString());//TODO удалить
        User updUser = userService.update(userId, UserDtoMapper.toUser(userDto));
        return UserDtoMapper.toUserDto(updUser);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) {
        log.debug("/get");
        return UserDtoMapper.toUserDto(userService.get(userId));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.debug("/delete");
        userService.delete(userId);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.debug("/getAll");
        return userService.getAll().stream().map(UserDtoMapper::toUserDto).collect(Collectors.toList());
    }
}