package com.marin.quotesdashboardbackend.repositories;

import com.marin.quotesdashboardbackend.entities.FriendConnection;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.enums.FriendConnectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendConnectionRepository extends JpaRepository<FriendConnection, Long> {

    List<FriendConnection> findByUserAndStatus(User user, FriendConnectionStatus status);

    List<FriendConnection> findByFriendAndStatus(User friend, FriendConnectionStatus status);

    int countByUserAndStatus(User friend, FriendConnectionStatus status);

    FriendConnection findByUserAndFriendAndStatus(User user, User friend, FriendConnectionStatus status);

    boolean existsByUserAndFriendAndStatus(User user, User friend, FriendConnectionStatus status);
}
