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
public class UserStorageInMemory implements UserStorage{
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User add(User user) {
        log.debug("/add");
        long newId = generateId();
        user.setId(newId);
        users.put(newId, user);
        return user;
    }

    @Override
    public User getById(long userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    private long generateId() {
        log.debug("/generateId");
        long maxId = users.values().stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return maxId + 1;
    }
}
