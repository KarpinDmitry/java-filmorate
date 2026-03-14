package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.FilmValidator;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("likeDbStorage") LikeStorage likeStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        FilmValidator.createValidator(film);
        Film savedFilm = filmStorage.create(film);

        // Перечитываем фильм из БД, чтобы загрузить жанры с name
        return findById(savedFilm.getId());
    }

    public Film update(Film newFilm) {
        FilmValidator.updateValidator(newFilm);

        if (!filmStorage.existsById(newFilm.getId())) {
            log.warn("Ошибка: фильм с ID={} не найден", newFilm.getId());
            throw new NotFoundException("Фильм с таким ID не найден");
        }

        Film existingFilm = filmStorage.findById(newFilm.getId()).orElseThrow(() ->
                new NotFoundException("Фильм с таким ID не найден"));

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

        return filmStorage.update(existingFilm);
    }

    public void delete(Integer filmId) {
        if (!filmStorage.existsById(filmId)) {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }

        filmStorage.delete(filmId);
    }

    public Film findById(Integer filmId) {
        Film film = filmStorage.findById(filmId).orElseThrow(() ->
                new NotFoundException("Фильм с id=" + filmId + " не найден"));

        film.setGenres(new LinkedHashSet<>(filmStorage.findGenresByFilmId(filmId)));

        return film;
    }

    public void addLike(Integer filmId, Integer userId) {
        if (!filmStorage.existsById(filmId)) {
            throw new NotFoundException("Фильм с id: " + filmId + " не найден");
        }
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }

        likeStorage.addLike(filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        if (!filmStorage.existsById(filmId)) {
            throw new NotFoundException("Фильм с id: " + filmId + " не найден");
        }
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }

        likeStorage.removeLike(filmId, userId);
    }

    public List<Integer> getLikes(Integer filmId) {
        if (!filmStorage.existsById(filmId)) {
            throw new NotFoundException("Фильм с id: " + filmId + " не найден");
        }

        return likeStorage.getLikes(filmId).stream().toList();
    }

    public List<Film> getTop(Integer count) {
        Map<Integer, Set<Integer>> filmLikes = likeStorage.getAllFilmLikes();
        Map<Integer, Integer> likesTop = new HashMap<>();

        for (Integer filmId : filmLikes.keySet()) {
            likesTop.put(filmId, filmLikes.get(filmId).size());
        }
        List<Integer> intResult = likesTop.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey).limit(count).toList();

        return intResult.stream().map(this::findById).toList();
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }
}
