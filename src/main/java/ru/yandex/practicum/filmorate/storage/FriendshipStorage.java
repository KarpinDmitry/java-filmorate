package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

public interface FriendshipStorage {
    void addFriend(Integer userId, Integer friendId);

    void removeFriend(Integer userId, Integer friendId);

    Set<Integer> getFriends(Integer userId);

    Set<Integer> getCommonFriends(Integer userId, Integer friendId);

    boolean existsById(Integer userId);
}
