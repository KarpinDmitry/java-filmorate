package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.mappers.FilmMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmMapper.class})
class FilmDbStorageTests {

    @Autowired
    private FilmDbStorage filmStorage;

    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Description");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(120);

        MpaRating mpa = new MpaRating();
        mpa.setId(1);
        mpa.setName("G");
        testFilm.setMpa(mpa);

        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Comedy");
        testFilm.setGenres(Set.of(genre));
    }

    @Test
    void testCreateAndFindById() {
        Film created = filmStorage.create(testFilm);

        Optional<Film> fetched = filmStorage.findById(created.getId());
        assertThat(fetched)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getName()).isEqualTo("Test Film");
                    assertThat(f.getMpa().getId()).isEqualTo(1);
                    assertThat(f.getGenres()).extracting("id").contains(1);
                });
    }

    @Test
    void testUpdate() {
        Film created = filmStorage.create(testFilm);

        created.setName("Updated Film");

        Genre genre = new Genre();
        genre.setId(2);
        genre.setName("Drama");
        created.setGenres(Set.of(genre));

        filmStorage.update(created);

        Optional<Film> fetched = filmStorage.findById(created.getId());
        assertThat(fetched)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getName()).isEqualTo("Updated Film");
                    assertThat(f.getGenres()).extracting("id").contains(2);
                });
    }

    @Test
    void testDelete() {
        Film created = filmStorage.create(testFilm);
        filmStorage.delete(created.getId());

        Optional<Film> fetched = filmStorage.findById(created.getId());
        assertThat(fetched).isEmpty();
    }

    @Test
    void testFindAll() {
        filmStorage.create(testFilm);

        Film film2 = new Film();
        film2.setName("Film2");
        film2.setDescription("Desc2");
        film2.setReleaseDate(LocalDate.of(2001, 2, 2));
        film2.setDuration(90);

        MpaRating mpa2 = new MpaRating();
        mpa2.setId(1);
        mpa2.setName("G");
        film2.setMpa(mpa2);

        Genre genre2 = new Genre();
        genre2.setId(1);
        genre2.setName("Comedy");
        film2.setGenres(Set.of(genre2));

        filmStorage.create(film2);

        List<Film> films = filmStorage.findAll();
        assertThat(films)
                .hasSizeGreaterThanOrEqualTo(2)
                .extracting("name")
                .contains("Test Film", "Film2");
    }

    @Test
    void testExistsById() {
        Film created = filmStorage.create(testFilm);

        assertThat(filmStorage.existsById(created.getId())).isTrue();
        assertThat(filmStorage.existsById(-1)).isFalse();
    }

    @Test
    void testFindGenresByFilmId() {
        Film created = filmStorage.create(testFilm);

        List<Genre> genres = filmStorage.findGenresByFilmId(created.getId());
        assertThat(genres)
                .extracting("id")
                .contains(1);
    }
}
