package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class UserStorageInMemory implements UserStorage {
    private static Long countId = 1L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User add(User user) {
        log.debug("/add");
        user.setId(countId);
        users.put(countId, user);
        countId++;
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        log.debug("/update");
        users.put(userId, user);
        return users.get(userId);
    }

    @Override
    public void delete(Long userId) {
        log.debug("/delete");
        users.remove(userId);
    }

    @Override
    public User get(long userId) {
        log.debug("/get");
        return users.get(userId);
    }

    @Override
    public List<User> getAll() {
        log.debug("/getAll");
        return new ArrayList<>(users.values());
    }
}