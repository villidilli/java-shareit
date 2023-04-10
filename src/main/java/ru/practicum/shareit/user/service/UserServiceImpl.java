package ru.practicum.shareit.user.service;

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

import static ru.practicum.shareit.exception.NotFoundException.NOT_FOUND_BY_ID;
import static ru.practicum.shareit.exception.ValidateException.DUPLICATE_EMAIL;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User create(User user, BindingResult br) {
        log.debug("/create");
        customValidate(user);
        annotationValidate(br);
        return userStorage.add(user);
    }

    @Override
    public void isExist(User user) {
        log.debug("/isExist");
        if(userStorage.getById(user.getId()) == null) throw new NotFoundException(NOT_FOUND_BY_ID);
    }

    @Override
    public void customValidate(User user) throws FieldConflictException {
        log.debug("/customValidate");
        boolean isHaveDuplicateEmail = userStorage.getAll().stream()
                .map(User::getEmail)
                .anyMatch(s -> s.equals(user.getEmail()));
        if(isHaveDuplicateEmail) throw new FieldConflictException(DUPLICATE_EMAIL);
    }

    @Override
    public void annotationValidate(BindingResult br) {
        log.debug("/annotationValidate");
        if(br.hasErrors()) throw new ValidateException(Converter.bindingResultToString(br));
    }

}