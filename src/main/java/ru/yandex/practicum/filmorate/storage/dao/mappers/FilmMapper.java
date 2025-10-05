package ru.yandex.practicum.filmorate.storage.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

@Component
public class FilmMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<Integer, Film> filmMap = new HashMap<>();

        int filmId = rs.getInt("film_id");

        Film film = filmMap.computeIfAbsent(filmId, id -> {
            Film f = new Film();
            f.setId(filmId);
            try {
                f.setName(rs.getString("name"));
                f.setDescription(rs.getString("description"));
                f.setDuration(rs.getInt("duration"));
                f.setReleaseDate(rs.getDate("release_date").toLocalDate());

                MpaRating mpa = new MpaRating();
                mpa.setId(rs.getInt("mpa_id"));
                mpa.setName(rs.getString("mpa_name"));
                f.setMpa(mpa);

                f.setGenres(new LinkedHashSet<>());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return f;
        });

        int genreId = rs.getInt("genre_id");
        if (genreId != 0) {
            Genre genre = new Genre();
            genre.setId(genreId);
            genre.setName(rs.getString("genre_name"));
            film.getGenres().add(genre);
        }

        return film;
    }
}
