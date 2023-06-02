package ru.practicum.shareit.user.service;

import org.springframework.validation.BindingResult;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto, BindingResult br);

    UserDto update(Long userId, UserDto userDto);

    UserDto get(Long userId);

    void delete(Long userId);

    List<UserDto> getAll();

    void isExist(Long userId);
}