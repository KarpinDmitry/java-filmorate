package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.inmemory.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.inmemory.InMemoryLikeStorage;
import ru.yandex.practicum.filmorate.storage.inmemory.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;


    @BeforeEach
    void setUp() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
                new InMemoryLikeStorage(), new InMemoryUserStorage()));
    }

    @Test
    void createFilmSuccess() {
        Film film = new Film();
        film.setName("FilmName");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film created = filmController.create(film);

        assertNotNull(created.getId());
        assertEquals("FilmName", created.getName());
    }

    @Test
    void createFilmInvalidNameThrows() {
        Film film = new Film();
        film.setName(" ");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void updateFilmSuccess() {
        Film film = new Film();
        film.setName("FilmName");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Film created = filmController.create(film);

        Film updated = new Film();
        updated.setId(created.getId());
        updated.setName("NewFilm");
        updated.setDescription("New description");
        updated.setReleaseDate(LocalDate.of(2010, 5, 5));
        updated.setDuration(150);

        Film result = filmController.update(updated);

        assertEquals("NewFilm", result.getName());
        assertEquals(150, result.getDuration());
    }

    @Test
    void updateFilmNotFoundThrows() {
        Film updated = new Film();
        updated.setId(999);
        updated.setName("Name");
        updated.setDescription("Description");
        updated.setReleaseDate(LocalDate.of(2000, 1, 1));
        updated.setDuration(100);

        assertThrows(NotFoundException.class, () -> filmController.update(updated));
    }
}
