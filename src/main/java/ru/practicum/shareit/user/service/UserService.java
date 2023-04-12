package ru.practicum.shareit.user.service;

import org.springframework.validation.BindingResult;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {

    User create(User user, BindingResult br);

    void EmailDuplicateValidate(String email, Long userId);
    void EmailNotBlankValidate(String email);

    void annotationValidate(BindingResult br);

    User update(Long userId, User user);

    User get(Long userId);

    void delete(Long userId);

    List<User> getAll();
}