package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage extends JpaRepository<User, Long> {

//    User add(User user);
//
//    User get(long userId);
//
//    List<User> getAll();
//
//    User update(Long userId, User user);
//
//    void delete(Long userId);
//
//    void isExist(Long userId);
//
//    void isExist(String email);
    User findFirstByEmail(String email);
}