package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получен запрос на получение всех пользователей. Текущее количество: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Запрос на создание пользователя: {}", user);

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации: некорректный email: {}", user.getEmail());
            throw new ValidationException("Ошибка: значение почты невалидно");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Ошибка валидации: некорректный логин: {}", user.getLogin());
            throw new ValidationException("Ошибка: значение логина невалидно");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пустое — устанавливаем имя равным логину: {}", user.getLogin());
            user.setName(user.getLogin());
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации: дата рождения в будущем или null: {}", user.getBirthday());
            throw new ValidationException("Ошибка: дата рождения не может быть в будущем");
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан с ID={}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Запрос на обновление пользователя: {}", newUser);

        if (newUser.getId() == null) {
            log.warn("Ошибка: не указан ID при обновлении пользователя");
            throw new ValidationException("Id должен быть указан");
        }
        if (!users.containsKey(newUser.getId())) {
            log.warn("Ошибка: пользователь с ID={} не найден", newUser.getId());
            throw new ValidationException("Пользователь с таким ID не найден");
        }

        User existingUser = users.get(newUser.getId());

        if (newUser.getEmail() != null && !newUser.getEmail().equals(existingUser.getEmail())) {
            boolean emailExists = users.values().stream()
                    .anyMatch(u -> u.getEmail().equals(newUser.getEmail()));
            if (emailExists) {
                log.warn("Ошибка: email {} уже используется другим пользователем", newUser.getEmail());
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            log.debug("Обновляем email пользователя ID={} с {} на {}", newUser.getId(),
                    existingUser.getEmail(), newUser.getEmail());
            existingUser.setEmail(newUser.getEmail());
        }

        if (newUser.getLogin() != null && !newUser.getLogin().isBlank()) {
            log.debug("Обновляем логин пользователя ID={} на {}", newUser.getId(), newUser.getLogin());
            existingUser.setLogin(newUser.getLogin());
        }

        if (newUser.getName() != null) {
            log.debug("Обновляем имя пользователя ID={} на {}", newUser.getId(), newUser.getName());
            existingUser.setName(newUser.getName());
        }

        if (newUser.getBirthday() != null) {
            if (newUser.getBirthday().isAfter(LocalDate.now())) {
                log.warn("Ошибка: дата рождения {} находится в будущем", newUser.getBirthday());
                throw new ValidationException("Ошибка: дата рождения не может быть в будущем");
            } else {
                log.debug("Обновляем дату рождения пользователя ID={} на {}", newUser.getId(), newUser.getBirthday());
                existingUser.setBirthday(newUser.getBirthday());
            }
        }

        log.info("Пользователь с ID={} успешно обновлён", newUser.getId());
        return existingUser;
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
