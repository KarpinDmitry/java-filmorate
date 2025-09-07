package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм успешно создан с id={}", film.getId());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        films.put(newFilm.getId(), newFilm);

        log.info("Фильм с ID={} успешно обновлён", newFilm.getId());
        return newFilm;
    }

    @Override
    public void delete(Integer filmId) {
        films.remove(filmId);
        log.info("Фильм с ID={} успешно удален", filmId);
    }

    @Override
    public Optional<Film> findById(Integer filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public List<Film> findAll() {
        return films.values().stream().toList();
    }

    public boolean existsById(Integer filmId) {
        return films.containsKey(filmId);
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
