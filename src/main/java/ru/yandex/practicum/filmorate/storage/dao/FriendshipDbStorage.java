package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository("friendshipDbStorage")
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Friendship> rowMapper;

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        String sql = "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, FriendshipStatus.UNCONFIRMED.name());

    }

    @Override
    public void confirmFriendship(Integer userId, Integer friendId) {
        String sql = "UPDATE friendships SET status = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, FriendshipStatus.CONFIRMED.name(), userId, friendId);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public Set<Integer> getFriends(Integer userId) {
        String sql = "SELECT friend_id FROM friendships WHERE user_id = ?";
        List<Integer> friends = jdbcTemplate.queryForList(sql, Integer.class, userId);
        return new HashSet<>(friends);
    }

    @Override
    public Set<Integer> getCommonFriends(Integer userId, Integer friendId) {
        String sql = """
                    SELECT f1.friend_id
                    FROM friendships f1
                    JOIN friendships f2 ON f1.friend_id = f2.friend_id
                    WHERE f1.user_id = ? AND f2.user_id = ?
                """;
        List<Integer> commonFriends = jdbcTemplate.queryForList(sql, Integer.class, userId, friendId);
        return new HashSet<>(commonFriends);
    }

    @Override
    public List<Friendship> getFriendRequests(Integer userId) {
        String sql = "SELECT * FROM friendships WHERE friend_id = ? AND status = ?";
        return jdbcTemplate.query(sql, rowMapper, userId, FriendshipStatus.UNCONFIRMED.name());
    }

    @Override
    public boolean existsById(Integer userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count > 0;
    }

    public List<Genre> findGenresByFilmId(Integer filmId) {
        String sql = """
                SELECT g.id, g.name
                FROM film_genres fg
                JOIN genres g ON fg.genre_id = g.id
                WHERE fg.film_id = ?
                ORDER BY g.id
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId);
    }
}

