package ru.practicum.shareit.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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

import static ru.practicum.shareit.exception.NotFoundException.USER_NOT_FOUND;
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
        annotationValidate(br);
        return toUserDto(userStorage.save(user));
    }

    @SneakyThrows
    @Override
    public UserDto update(Long userId, UserDto userDto) {
        log.debug("/update");
        isExist(userId);
        User existedUser = userStorage.findById(userId).get();
        User userWithUpdate = toUser(userDto);
        User updatedUser = setNewFields(existedUser, userWithUpdate);
        return toUserDto(userStorage.save(updatedUser));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto get(Long userId) throws NotFoundException {
        log.debug("/get");
        isExist(userId);
        return toUserDto(userStorage.findById(userId).get());
    }

    @Override
    public void delete(Long userId) {
        log.debug("/delete");
        isExist(userId);
        userStorage.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        log.debug("/getAll");
        return userStorage.findAll().stream().map(UserDtoMapper::toUserDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public void isExist(Long userId) throws NotFoundException {
        log.debug("/isExist");
        if(!userStorage.existsById(userId)) throw new NotFoundException(USER_NOT_FOUND);
    }

    private User setNewFields(User existedUser, User userWithUpdate) {
        Map<String, String> existedUserMap = objectMapper.convertValue(existedUser, Map.class);
        Map<String, String> fieldsToUpdate = getFieldsToUpdate(userWithUpdate);
        existedUserMap.putAll(fieldsToUpdate);
        return objectMapper.convertValue(existedUserMap, User.class);
    }

    private void annotationValidate(BindingResult br) {
        log.debug("/annotationValidate");
        if (br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
    }

    private Map<String, String> getFieldsToUpdate(User user) {
        log.debug("/getFieldsToUpdate");
        Map<String, String> mapWithNullFields = objectMapper.convertValue(user, Map.class);
        return mapWithNullFields.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}