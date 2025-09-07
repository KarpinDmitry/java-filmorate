package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User create(User user);

    User update(User user);

    void delete(Integer userId);

    Optional<User> findById(Integer userId);

    List<User> findAll();

    boolean existsById(Integer userId);

    boolean existsEmail(String email);
}
