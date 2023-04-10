package ru.practicum.shareit.user.service;

import org.springframework.validation.BindingResult;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    User create(User user, BindingResult br);

    void isExist(User user);

    void customValidate(User user);

    void annotationValidate(BindingResult br);
}