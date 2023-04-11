package ru.practicum.shareit.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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

    @Override
    public User update(Long userId, Map<String, String> valuesToUpdate) {
        log.debug("/update");
        isExist(userId);
        String emailToUpdate = valuesToUpdate.get("email");
        if(emailToUpdate != null) customEmailValidate(emailToUpdate, userId);
        User existedUser = userStorage.get(userId);
        Map<String, String> existedUserMap = objectMapper.convertValue(existedUser, Map.class);
        existedUserMap.putAll(valuesToUpdate);
        User updatedUser = objectMapper.convertValue(existedUserMap, User.class);
        return userStorage.update(userId, updatedUser);
    }

    @Override
    public User get(Long userId) {
        isExist(userId);
        return userStorage.get(userId);
    }

    @Override
    public void delete(Long userId) {
        isExist(userId);
        userStorage.delete(userId);
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public void isExist(Long userId) {
        log.debug("/isExist");
        if(userStorage.get(userId) == null) throw new NotFoundException(NOT_FOUND_BY_ID);
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