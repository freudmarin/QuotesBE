package com.marin.socialnetwork.repositories;

import com.marin.socialnetwork.entities.FriendConnection;
import com.marin.socialnetwork.entities.User;
import com.marin.socialnetwork.enums.FriendConnectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendConnectionRepository extends JpaRepository<FriendConnection, Long> {

    List<FriendConnection> findByUserAndStatus(User user, FriendConnectionStatus status);

    List<FriendConnection> findByFriendAndStatus(User friend, FriendConnectionStatus status);

    int countByUserAndStatus(User friend, FriendConnectionStatus status);

    FriendConnection findByUserAndFriendAndStatus(User user, User friend, FriendConnectionStatus status);

    boolean existsByUserAndFriendAndStatus(User user, User friend, FriendConnectionStatus status);
}
