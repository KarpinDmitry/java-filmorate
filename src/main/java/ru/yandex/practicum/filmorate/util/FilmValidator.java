package ru.yandex.practicum.filmorate.util;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class FilmValidator {
    public static void createValidator(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Ошибка: имя фильма невалидно");
            throw new ValidationException("Ошибка: имя фильма невалидно");
        }

        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.warn("Ошибка: описание фильма невалидно");
            throw new ValidationException("Ошибка: описание фильма невалидно");
        }

        if (film.getReleaseDate() == null
                || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка: дата релиза невалидна {}", film.getReleaseDate());
            throw new ValidationException("Ошибка: дата релиза невалидна");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            log.warn("Ошибка: продолжительность невалидна {}", film.getDuration());
            throw new ValidationException("Ошибка: продолжительность невалидна");
        }
    }

    public static void updateValidator(Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Ошибка: не указан ID при обновлении фильма");
            throw new ValidationException("Id должен быть указан");
        }
    }
}
