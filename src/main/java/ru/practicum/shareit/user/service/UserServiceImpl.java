package ru.practicum.shareit.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.NotFoundException.USER_NOT_FOUND;
import static ru.practicum.shareit.exception.ValidateException.DUPLICATE_EMAIL;
import static ru.practicum.shareit.exception.ValidateException.EMAIL_NOT_BLANK;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final ObjectMapper objectMapper;

    @Override
    public UserDto create(UserDto userDto, BindingResult br) {
        log.debug("/create");
        User user = UserDtoMapper.toUser(userDto);
        emailDuplicateValidate(user.getEmail(), user.getId());
        emailNotBlankValidate(user.getEmail());
        annotationValidate(br);
        user = userStorage.add(user);
        return UserDtoMapper.toUserDto(user);
    }

    @SneakyThrows
    @Override
    public UserDto update(Long userId, UserDto userDto) {
        log.debug("/update");
        User user = UserDtoMapper.toUser(userDto);
        userStorage.isExist(userId);
        emailDuplicateValidate(user.getEmail(), userId);
        User savedUser = userStorage.get(userId);
        Map<String, String> userMap = objectMapper.convertValue(user, Map.class);
        Map<String, String> valuesToUpdate = userMap.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        objectMapper.updateValue(savedUser, valuesToUpdate);
        user = userStorage.update(userId, savedUser);
        return UserDtoMapper.toUserDto(user);
    }

    @Override
    public UserDto get(Long userId) throws NotFoundException {
        log.debug("/get");
        userStorage.isExist(userId);
        return UserDtoMapper.toUserDto(userStorage.get(userId));
    }

    @Override
    public void delete(Long userId) {
        log.debug("/delete");
        userStorage.isExist(userId);
        userStorage.delete(userId);
    }

    @Override
    public List<UserDto> getAll() {
        log.debug("/getAll");
        return userStorage.getAll().stream().map(UserDtoMapper::toUserDto).collect(Collectors.toList());
    }

    public void emailDuplicateValidate(String email, Long userId) throws FieldConflictException {
        log.debug("/emailDuplicateValid");
        boolean isHaveDuplicateEmail = userStorage.getAll().stream()
                .filter(savedUser -> !Objects.equals(userId, savedUser.getId()))
                .map(User::getEmail)
                .anyMatch(s -> s.equals(email));
        if (isHaveDuplicateEmail) throw new FieldConflictException(DUPLICATE_EMAIL);
    }

    @Override
    public void emailNotBlankValidate(String email) {
        log.debug("/emailNotBlankValid");
        if (email == null || email.isBlank()) throw new ValidateException(EMAIL_NOT_BLANK);
    }

    @Override
    public void annotationValidate(BindingResult br) {
        log.debug("/annotationValidate");
        if (br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
    }
}