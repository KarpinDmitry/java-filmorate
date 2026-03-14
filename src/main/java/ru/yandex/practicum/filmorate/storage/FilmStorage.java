package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    void delete(Integer filmId);

    Optional<Film> findById(Integer filmId);

    List<Film> findAll();

    boolean existsById(Integer filmId);

    List<Genre> findGenresByFilmId(Integer filmId);
}
