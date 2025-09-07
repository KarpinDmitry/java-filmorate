package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFriendshipStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(new UserService(new InMemoryUserStorage(),
                new InMemoryFriendshipStorage()));
    }

    @Test
    void createUserSuccess() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userController.create(user);

        assertNotNull(created.getId());
        assertEquals("login", created.getLogin());
    }

    @Test
    void createUserInvalidEmailThrows() {
        User user = new User();
        user.setEmail("invalid");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void updateUserSuccess() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User created = userController.create(user);

        User updated = new User();
        updated.setId(created.getId());
        updated.setEmail("new@mail.com");
        updated.setLogin("newLogin");
        updated.setName("NewName");
        updated.setBirthday(LocalDate.of(1990, 5, 5));

        User result = userController.update(updated);

        assertEquals("new@mail.com", result.getEmail());
        assertEquals("newLogin", result.getLogin());
    }

    @Test
    void updateUserNotFoundThrows() {
        User updated = new User();
        updated.setId(999);
        updated.setEmail("mail@mail.com");
        updated.setLogin("login");
        updated.setName("Name");
        updated.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.update(updated));
    }
}
