package ru.yandex.practicum.filmorate.util;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.Set;

@Slf4j
public class FilmValidator {

    public static void createValidator(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Ошибка: имя фильма невалидно");
            throw new ValidationException("Имя фильма не может быть пустым");
        }

        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.warn("Ошибка: описание фильма невалидно (длина {})",
                    film.getDescription() != null ? film.getDescription().length() : 0);
            throw new ValidationException("Описание не может быть пустым и длиннее 200 символов");
        }

        if (film.getReleaseDate() == null
                || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка: дата релиза невалидна {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }

        if (film.getDuration() == null || film.getDuration() <= 0) {
            log.warn("Ошибка: продолжительность невалидна {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

        MpaRating mpa = film.getMpa();
        if (mpa != null && (mpa.getId() < 1 || mpa.getId() > 5)) {
            log.warn("Ошибка: рейтинг MPA невалиден {}", mpa);
            throw new NotFoundException("Некорректный рейтинг MPA. Допустимые id: 1–5");
        }

        Set<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            for (Genre genre : genres) {
                if (genre == null || genre.getId() < 1 || genre.getId() > 6) {
                    log.warn("Ошибка: жанр фильма невалиден {}", genre);
                    throw new NotFoundException("Некорректный жанр. Допустимые id: 1–6");
                }
            }
        }
    }

    public static void updateValidator(Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Ошибка: не указан ID при обновлении фильма");
            throw new ValidationException("Id должен быть указан при обновлении фильма");
        }
    }
}
