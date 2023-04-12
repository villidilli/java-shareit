package ru.practicum.shareit.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

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
    public User create(User user, BindingResult br) {
        log.debug("/create");
        EmailDuplicateValidate(user.getEmail(), user.getId());
        EmailNotBlankValidate(user.getEmail());
        annotationValidate(br);
        return userStorage.add(user);
    }

    @SneakyThrows
    @Override
    public User update(Long userId, User userFromDto) {
        log.debug("/update");
        EmailDuplicateValidate(userFromDto.getEmail(), userId);
        isExist(userId);
        User savedUser = get(userId);
        Map<String, String> userMap = objectMapper.convertValue(userFromDto, Map.class);
        Map<String, String> valuesToUpdate = userMap.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        objectMapper.updateValue(savedUser, valuesToUpdate);
        return userStorage.update(userId, savedUser);
    }

    @Override
    public User get(Long userId) throws NotFoundException {
        log.debug("/get");
        isExist(userId);
        return userStorage.get(userId);
    }

    @Override
    public void delete(Long userId) {
        log.debug("/delete");
        isExist(userId);
        userStorage.delete(userId);
    }

    @Override
    public List<User> getAll() {
        log.debug("/getAll");
        return userStorage.getAll();
    }

    @Override
    public void isExist(Long userId) {
        log.debug("/isExist");
        if (userStorage.get(userId) == null) throw new NotFoundException(USER_NOT_FOUND);
    }

    @Override
    public void EmailDuplicateValidate(String email, Long userId) throws FieldConflictException {
        log.debug("/emailDuplicateValid");
        boolean isHaveDuplicateEmail = userStorage.getAll().stream()
                .filter(savedUser -> !Objects.equals(userId, savedUser.getId()))
                .map(User::getEmail)
                .anyMatch(s -> s.equals(email));
        if (isHaveDuplicateEmail) throw new FieldConflictException(DUPLICATE_EMAIL);
    }

    @Override
    public void EmailNotBlankValidate(String email) {
        log.debug("/emailNotBlankValid");
        if (email == null || email.isBlank()) throw new ValidateException(EMAIL_NOT_BLANK);
    }

    @Override
    public void annotationValidate(BindingResult br) {
        log.debug("/annotationValidate");
        if (br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
    }
}