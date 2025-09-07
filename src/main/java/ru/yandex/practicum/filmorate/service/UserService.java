package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.UserValidator;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    public User create(User user) {
        UserValidator.userCreate(user);

        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пустое — устанавливаем имя равным логину: {}", user.getLogin());
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User update(User newUser) {
        UserValidator.updateValidator(newUser);

        if (!userStorage.existsById(newUser.getId())) {
            log.warn("Ошибка: пользователь с ID={} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с таким ID не найден");
        }

        User existingUser = userStorage.findById(newUser.getId()).orElseThrow(() ->
                new NotFoundException("User с id " + newUser.getId() + " не найден"));

        if (newUser.getEmail() != null && !newUser.getEmail().equals(existingUser.getEmail())) {
            boolean emailExists = userStorage.existsEmail(newUser.getEmail());
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

        userStorage.update(existingUser);
        log.info("Пользователь с ID={} успешно обновлён", newUser.getId());

        return existingUser;
    }

    public void delete(Integer userId) {
        if (!userStorage.existsById(userId)) {
            //throw new NotFoundException("User c id " + userId + " не найден");
            return;
        }

        for (User user : getFriends(userId)) {
            friendshipStorage.removeFriend(user.getId(), userId);
        }

        userStorage.delete(userId);

    }

    public User findById(Integer userId) {
        return userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (!userStorage.existsById(userId) || !userStorage.existsById(friendId)) {
            throw new NotFoundException("User not found");
        }
        friendshipStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        if (!userStorage.existsById(userId) || !userStorage.existsById(friendId)) {
            throw new NotFoundException("User not found");
        }
        friendshipStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Integer userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("User not found");

        }
        return friendshipStorage.getFriends(userId).stream().map(this::findById).toList();
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        if (!userStorage.existsById(userId) || !userStorage.existsById(friendId)) {
            throw new NotFoundException("User not found");
        }
        return friendshipStorage.getCommonFriends(userId, friendId).stream().map(this::findById).toList();
    }
}
