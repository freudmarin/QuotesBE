package com.marin.quotesdashboardbackend.services;


import com.marin.quotesdashboardbackend.dtos.DTOMappings;
import com.marin.quotesdashboardbackend.dtos.FriendConnectionDTO;
import com.marin.quotesdashboardbackend.entities.FriendConnection;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.enums.FriendConnectionStatus;
import com.marin.quotesdashboardbackend.repositories.FriendConnectionRepository;
import com.marin.quotesdashboardbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendConnectionService {


    private final FriendConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    private User getLoggedInUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    }

    public FriendConnectionDTO sendFriendRequest(Long friendId) {
        User user = getLoggedInUser();
        User friend = userRepository.findById(friendId).orElseThrow();

        FriendConnection connection = new FriendConnection(null, user, friend, FriendConnectionStatus.PENDING);
        return DTOMappings.fromFriendConnectionToFriendConnectionDTO(connectionRepository.save(connection));
    }

    public FriendConnectionDTO respondToFriendRequest(Long connectionId, FriendConnectionStatus status) {
        FriendConnection connection = connectionRepository.findById(connectionId).orElseThrow();
        User loggedInUser = getLoggedInUser();

        if (!connection.getFriend().equals(loggedInUser)) {
            throw new IllegalArgumentException("You can only respond to requests sent to you.");
        }

        connection.setStatus(status);
        return DTOMappings.fromFriendConnectionToFriendConnectionDTO(connectionRepository.save(connection));
    }

    public List<FriendConnection> getFriends() {
        User user = getLoggedInUser();
        List<FriendConnection> friends = connectionRepository.findByUserAndStatus(user, FriendConnectionStatus.ACCEPTED);
        friends.addAll(connectionRepository.findByFriendAndStatus(user, FriendConnectionStatus.ACCEPTED));
        return friends;
    }

    public List<FriendConnectionDTO> getFriendsWithDTO() {
       return getFriends().stream().map(DTOMappings::fromFriendConnectionToFriendConnectionDTO)
                .toList();
    }

    public List<FriendConnectionDTO> getPendingRequests() {
        User user = getLoggedInUser();
        return connectionRepository.findByFriendAndStatus(user, FriendConnectionStatus.PENDING)
                .stream().map(DTOMappings::fromFriendConnectionToFriendConnectionDTO)
                .toList();
    }
}
