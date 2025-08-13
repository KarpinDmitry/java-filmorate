package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll(){
        return films.values();
    }
    @PostMapping
    public Film create(@RequestBody Film film){
        log.info("Запрос: создать фильм {}", film);
        if (film.getName() == null || film.getName().isBlank()){
            log.warn("Ошибка: имя фильма невалидно");
            throw new ValidationException("Ошибка: имя фильма невалидно");
        }

        if (film.getDescription() == null || film.getDescription().length() > 200){
            log.warn("Ошибка: описание фильма невалидно");
            throw new ValidationException("Ошибка: описание фильма невалидно");
        }

        if (film.getReleaseDate() == null
                || film.getReleaseDate().isBefore(LocalDate.of(1985,12,28))){
            log.warn("Ошибка: дата релиза невалидна {}", film.getReleaseDate());
            throw new ValidationException("Ошибка: дата релиза невалидна");
        }
        if (film.getDuration() == null || film.getDuration() <= 0){
            log.warn("Ошибка: продолжительность невалидна {}", film.getDuration());
            throw new ValidationException("Ошибка: продолжительность невалидна");
        }

        log.info("Фильм успешно создан с id={}", film.getId());

        film.setId(getNextId());
        films.put(film.getId(),film);

        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Запрос на обновление фильма: {}", newFilm);

        if (newFilm.getId() == null) {
            log.warn("Ошибка: не указан ID при обновлении фильма");
            throw new ValidationException("Id должен быть указан");
        }
        if (!films.containsKey(newFilm.getId())) {
            log.warn("Ошибка: фильм с ID={} не найден", newFilm.getId());
            throw new ValidationException("Фильм с таким ID не найден");
        }

        Film existingFilm = films.get(newFilm.getId());

        if (newFilm.getName() != null && !newFilm.getName().isBlank()) {
            log.debug("Обновляем название фильма ID={} на '{}'", newFilm.getId(), newFilm.getName());
            existingFilm.setName(newFilm.getName());
        }

        if (newFilm.getDescription() != null && newFilm.getDescription().length() <= 200) {
            log.debug("Обновляем описание фильма ID={} (длина {} символов)",
                    newFilm.getId(), newFilm.getDescription().length());
            existingFilm.setDescription(newFilm.getDescription());
        }

        if (newFilm.getReleaseDate() != null
                && newFilm.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            log.debug("Обновляем дату релиза фильма ID={} на {}",
                    newFilm.getId(), newFilm.getReleaseDate());
            existingFilm.setReleaseDate(newFilm.getReleaseDate());
        }

        if (newFilm.getDuration() != null && newFilm.getDuration() > 0) {
            log.debug("Обновляем продолжительность фильма ID={} на {} минут",
                    newFilm.getId(), newFilm.getDuration());
            existingFilm.setDuration(newFilm.getDuration());
        }

        log.info("Фильм с ID={} успешно обновлён", newFilm.getId());
        return existingFilm;
    }


    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
