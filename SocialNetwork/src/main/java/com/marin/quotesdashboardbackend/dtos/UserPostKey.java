package com.marin.quotesdashboardbackend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;


@Getter
@Setter
public class UserPostKey {
    private Long userId;
    private Long postId;

    public UserPostKey(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPostKey that = (UserPostKey) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(postId, that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, postId);
    }
}
