package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Map;

@Data
@EqualsAndHashCode(of = "id")
public class User {
    Integer id;
    @Email(message = "Некорректный формат email")
    String email;
    String login;
    String name;
    LocalDate birthday;
    private Map<Long, FriendshipStatus> friends;

}
