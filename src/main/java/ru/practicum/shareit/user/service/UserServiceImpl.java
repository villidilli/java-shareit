package ru.practicum.shareit.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.ValidateException.EMAIL_NOT_BLANK;
import static ru.practicum.shareit.user.dto.UserDtoMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final ObjectMapper objectMapper;

    @Override
    public UserDto create(UserDto userDto, BindingResult br) {
        log.debug("/create");
        User user = toUser(userDto);
        emailDuplicateValidate(user.getEmail(), user.getId());
        emailNotBlankValidate(user.getEmail());
        annotationValidate(br);
        User createdUser = userStorage.save(user);
        return toUserDto(createdUser);
    }

    @SneakyThrows
    @Override
    public UserDto update(Long userId, UserDto userDto) {
        log.debug("/update");
        userStorage.isExist(userId);
        User userWithUpdate = toUser(userDto);
        emailDuplicateValidate(userWithUpdate.getEmail(), userId);
        Map<String, String> fieldsToUpdate = getFieldsToUpdate(userWithUpdate);
        User existedUser = userStorage.get(userId);
        Map<String, String> existedUserMap = objectMapper.convertValue(existedUser, Map.class);
        existedUserMap.putAll(fieldsToUpdate);
        User updatedUser = objectMapper.convertValue(existedUserMap, User.class);
        return toUserDto(userStorage.update(userId, updatedUser));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto get(Long userId) throws NotFoundException {
        log.debug("/get");
        userStorage.isExist(userId);
        return toUserDto(userStorage.get(userId));
    }

    @Override
    public void delete(Long userId) {
        log.debug("/delete");
        userStorage.isExist(userId);
        userStorage.delete(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        log.debug("/getAll");
        return userStorage.getAll().stream().map(UserDtoMapper::toUserDto).collect(Collectors.toList());
    }

    public void emailDuplicateValidate(String email, Long userId) throws FieldConflictException {
        log.debug("/emailDuplicateValid");
        if (userId == null) {
            userStorage.isExist(email);
            return;
        }
        if (!userStorage.get(userId).getEmail().equals(email)) {
            userStorage.isExist(email);
        }
    }

    private void emailNotBlankValidate(String email) {
        log.debug("/emailNotBlankValid");
        if (email == null || email.isBlank()) throw new ValidateException(EMAIL_NOT_BLANK);
    }

    private void annotationValidate(BindingResult br) {
        log.debug("/annotationValidate");
        if (br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
    }

    private Map<String, String> getFieldsToUpdate(User user) {
        Map<String, String> mapWithNullFields = objectMapper.convertValue(user, Map.class);
        return mapWithNullFields.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}