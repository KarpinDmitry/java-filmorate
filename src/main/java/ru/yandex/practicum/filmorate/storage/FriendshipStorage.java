package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface FriendshipStorage {
    // Заявка в друзья
    void addFriend(Integer userId, Integer friendId);

    // подтвердить заявку
    void confirmFriendship(Integer userId, Integer friendId);

    void removeFriend(Integer userId, Integer friendId);

    Set<Integer> getFriends(Integer userId);

    // только confirmed
    Set<Integer> getCommonFriends(Integer userId, Integer friendId);

    // получить список всех заявок
    List<Friendship> getFriendRequests(Integer userId);

    boolean existsById(Integer userId);

    List<Genre> findGenresByFilmId(Integer filmId);
}
