package ru.practicum.shareit.user.service;

import org.springframework.validation.BindingResult;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto, BindingResult br);

    void emailNotBlankValidate(String email);

    void annotationValidate(BindingResult br);

    UserDto update(Long userId, UserDto userDto);

    UserDto get(Long userId);

    void delete(Long userId);

    List<UserDto> getAll();
}