package ru.yandex.practicum.filmorate.util;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class UserValidator {
    public static void userCreate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации: некорректный email: {}", user.getEmail());
            throw new ValidationException("Ошибка: значение почты невалидно");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Ошибка валидации: некорректный логин: {}", user.getLogin());
            throw new ValidationException("Ошибка: значение логина невалидно");
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации: дата рождения в будущем или null: {}", user.getBirthday());
            throw new ValidationException("Ошибка: дата рождения не может быть в будущем");
        }
    }

    public static void updateValidator(User newUser) {
        if (newUser.getId() == null) {
            log.warn("Ошибка: не указан ID при обновлении пользователя");
            throw new ValidationException("Id должен быть указан");
        }
    }
}
