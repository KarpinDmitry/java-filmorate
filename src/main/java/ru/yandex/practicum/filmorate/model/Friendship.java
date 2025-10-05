package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Friendship {
    private Integer userId;
    private Integer friendId;
    @Setter
    private FriendshipStatus status;

    public Friendship(Integer userId, Integer friendId, FriendshipStatus status) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
    }

}
