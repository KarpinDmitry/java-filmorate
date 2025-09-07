package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InMemoryFriendshipStorage implements FriendshipStorage {
    private Map<Integer, Set<Integer>> friends = new HashMap<>();

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        friends.computeIfAbsent(userId, s -> new HashSet<>()).add(friendId);
        friends.computeIfAbsent(friendId, s -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        if (friends.containsKey(userId)) {
            friends.get(userId).remove(friendId);
        }
        if (friends.containsKey(friendId)) {
            friends.get(friendId).remove(userId);
        }
        // удаляем список если он пуст
        if (friends.get(userId) == null || friends.get(userId).isEmpty()) {
            friends.remove(userId);
        }
        if (friends.get(friendId) == null || friends.get(friendId).isEmpty()) {
            friends.remove(friendId);
        }
    }

    @Override
    public Set<Integer> getFriends(Integer userId) {
        return new HashSet<>(friends.getOrDefault(userId, Set.of()));
    }

    @Override
    public Set<Integer> getCommonFriends(Integer userId, Integer friendId) {
        Set<Integer> userFriends;
        if (!friends.containsKey(userId)) {
            return new HashSet<>();
        }
        Set<Integer> friendFriends;
        if (!friends.containsKey(friendId)) {
            return new HashSet<>();
        }

        userFriends = friends.get(userId);
        friendFriends = friends.get(friendId);
        Set<Integer> commonFriends;

        commonFriends = userFriends.stream().filter(
                id -> friendFriends.contains(id)).collect(Collectors.toSet());

        return commonFriends;
    }

    public boolean existsById(Integer userId) {
        return friends.containsKey(userId);
    }
}
