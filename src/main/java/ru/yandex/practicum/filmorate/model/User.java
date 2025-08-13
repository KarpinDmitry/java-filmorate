package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = "id")
public class User {
    Integer id;
    @Email(message = "Некорректный формат email")
    String email;
    String login;
    String name;
    LocalDate birthday;

}
