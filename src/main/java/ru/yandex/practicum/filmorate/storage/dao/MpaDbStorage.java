package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<MpaRating> findAll() {
        String sql = "SELECT id, name FROM mpa ORDER BY id";
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    @Override
    public Optional<MpaRating> findById(Integer id) {
        String sql = "SELECT id, name FROM mpa WHERE id = ?";
        List<MpaRating> result = jdbcTemplate.query(sql, this::mapRowToMpa, id);
        return result.stream().findFirst();
    }

    private MpaRating mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        MpaRating mpa = new MpaRating();
        mpa.setId(rs.getInt("id"));
        mpa.setName(rs.getString("name"));
        return mpa;
    }
}
