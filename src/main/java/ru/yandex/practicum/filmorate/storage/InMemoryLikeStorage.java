package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryLikeStorage implements LikeStorage {
    private Map<Integer, Set<Integer>> filmLikes = new HashMap<>();

    @Override
    public void addLike(Integer filmId, Integer userId) {
        filmLikes.computeIfAbsent(filmId, s -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        Set<Integer> likes = filmLikes.get(filmId);
        if (likes != null) {
            likes.remove(userId);
        }
    }

    @Override
    public Set<Integer> getLikes(Integer filmId) {
        return new HashSet<>(filmLikes.getOrDefault(filmId, Set.of()));
    }

    @Override
    public Map<Integer, Set<Integer>> getAllFilmLikes() {
        return new HashMap<>(filmLikes);
    }
}
