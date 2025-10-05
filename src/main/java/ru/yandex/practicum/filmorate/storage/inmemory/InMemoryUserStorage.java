package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан с ID={}", user.getId());

        return user;
    }

    @Override
    public User update(User newUser) {
        users.put(newUser.getId(), newUser);
        log.info("User с ID={} успешно обновлён", newUser.getId());

        return newUser;
    }

    @Override
    public void delete(Integer userId) {
        users.remove(userId);
        log.info("User с ID={} успешно удален", userId);
    }

    @Override
    public Optional<User> findById(Integer userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    public boolean existsById(Integer userId) {
        return users.containsKey(userId);
    }

    @Override
    public boolean existsEmail(String email) {
        return users.values().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
