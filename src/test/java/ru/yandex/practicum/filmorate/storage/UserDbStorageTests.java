package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.mappers.UserMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserMapper.class})
class UserDbStorageTests {

    @Autowired
    private UserDbStorage userStorage;

    @Test
    void testCreateAndFindById() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("loginTest");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User created = userStorage.create(user);

        Optional<User> fetched = userStorage.findById(created.getId());
        assertThat(fetched)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u.getEmail()).isEqualTo("test@example.com");
                    assertThat(u.getLogin()).isEqualTo("loginTest");
                    assertThat(u.getName()).isEqualTo("Test User");
                });
    }

    @Test
    void testUpdate() {
        User user = new User();
        user.setEmail("update@example.com");
        user.setLogin("loginUpdate");
        user.setName("Update User");
        user.setBirthday(LocalDate.of(1991, 2, 2));

        User created = userStorage.create(user);

        created.setName("Updated Name");
        created.setEmail("updated@example.com");
        userStorage.update(created);

        Optional<User> fetched = userStorage.findById(created.getId());
        assertThat(fetched)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u.getName()).isEqualTo("Updated Name");
                    assertThat(u.getEmail()).isEqualTo("updated@example.com");
                });
    }

    @Test
    void testDelete() {
        User user = new User();
        user.setEmail("delete@example.com");
        user.setLogin("loginDelete");
        user.setName("To Delete");
        user.setBirthday(LocalDate.of(1992, 3, 3));

        User created = userStorage.create(user);
        userStorage.delete(created.getId());

        Optional<User> fetched = userStorage.findById(created.getId());
        assertThat(fetched).isEmpty();
    }

    @Test
    void testFindAll() {
        User user1 = new User();
        user1.setEmail("a@example.com");
        user1.setLogin("loginA");
        user1.setName("User A");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        User user2 = new User();
        user2.setEmail("b@example.com");
        user2.setLogin("loginB");
        user2.setName("User B");
        user2.setBirthday(LocalDate.of(2001, 2, 2));

        userStorage.create(user1);
        userStorage.create(user2);

        List<User> users = userStorage.findAll();
        assertThat(users)
                .hasSizeGreaterThanOrEqualTo(2)
                .extracting("login")
                .contains("loginA", "loginB");
    }

    @Test
    void testExistsById() {
        User user = new User();
        user.setEmail("exists@example.com");
        user.setLogin("loginExists");
        user.setName("Exists User");
        user.setBirthday(LocalDate.of(1993, 4, 4));

        User created = userStorage.create(user);

        assertThat(userStorage.existsById(created.getId())).isTrue();
        assertThat(userStorage.existsById(-1)).isFalse();
    }

    @Test
    void testExistsEmail() {
        User user = new User();
        user.setEmail("emailtest@example.com");
        user.setLogin("loginEmail");
        user.setName("Email User");
        user.setBirthday(LocalDate.of(1994, 5, 5));

        userStorage.create(user);

        assertThat(userStorage.existsEmail("emailtest@example.com")).isTrue();
        assertThat(userStorage.existsEmail("notfound@example.com")).isFalse();
    }
}
