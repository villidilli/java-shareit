package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {
    User add(User user);

    User getById(long userId);

    List<User> getAll();
}
