package ru.practicum.shareit.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.exception.FieldConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.utils.Converter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.NotFoundException.NOT_FOUND_BY_ID;
import static ru.practicum.shareit.exception.ValidateException.DUPLICATE_EMAIL;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final ObjectMapper objectMapper;

    @Override
    public User create(User user, BindingResult br) {
        log.debug("/create");
        customEmailValidate(user.getEmail(), user.getId());
        annotationValidate(br);
        return userStorage.add(user);
    }

    @SneakyThrows
    @Override
    public User update(Long userId, User userFromDto) {
        log.debug("/update");
        User savedUser = get(userId);
        Map<String, String> userMap = objectMapper.convertValue(userFromDto, Map.class);
        Map<String, String> valuesToUpdate = userMap.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        customEmailValidate(userFromDto.getEmail(), userId);
        objectMapper.updateValue(savedUser, valuesToUpdate);
        return userStorage.update(userId, savedUser);
    }

    @Override
    public User get(Long userId) throws NotFoundException {
        User returnedUser = userStorage.get(userId);
        if(returnedUser == null) throw new NotFoundException(NOT_FOUND_BY_ID);
        return returnedUser;
    }

    @Override
    public void delete(Long userId) {
        userStorage.delete(get(userId).getId());
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public void customEmailValidate(String email, Long userId) throws FieldConflictException {
        log.debug("/customValidate");
        boolean isHaveDuplicateEmail = userStorage.getAll().stream()
                .filter(savedUser -> !Objects.equals(userId, savedUser.getId()))
                .map(User::getEmail)
                .anyMatch(s -> s.equals(email));
        if(isHaveDuplicateEmail) throw new FieldConflictException(DUPLICATE_EMAIL);
    }

    @Override
    public void annotationValidate(BindingResult br) {
        log.debug("/annotationValidate");
        if(br.hasErrors()) throw new ValidateException(Converter.bindingResultToString(br));
    }
}