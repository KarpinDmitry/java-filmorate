package ru.yandex.practicum.filmorate.storage;

import java.util.Map;
import java.util.Set;

public interface LikeStorage {
    void addLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);

    Set<Integer> getLikes(Integer filmId);

    Map<Integer, Set<Integer>> getAllFilmLikes();
}
