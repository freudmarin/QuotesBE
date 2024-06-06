package com.marin.socialnetwork.services;

import com.marin.socialnetwork.dtos.DTOMappings;
import com.marin.socialnetwork.dtos.FriendConnectionDTO;
import com.marin.socialnetwork.entities.FriendConnection;
import com.marin.socialnetwork.entities.User;
import com.marin.socialnetwork.enums.FriendConnectionStatus;
import com.marin.socialnetwork.repositories.FriendConnectionRepository;
import com.marin.socialnetwork.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
        User friend = userRepository.findById(friendId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        FriendConnection connection = new FriendConnection(null, user, friend, FriendConnectionStatus.PENDING);
        return DTOMappings.INSTANCE.toFriendConnectionDTO(connectionRepository.save(connection));
    }

    public FriendConnectionDTO respondToFriendRequest(Long connectionId, FriendConnectionStatus status) {
        FriendConnection connection = connectionRepository.findById(connectionId).orElseThrow(() -> new EntityNotFoundException("Connection not found"));
        User loggedInUser = getLoggedInUser();

        if (!connection.getFriend().equals(loggedInUser)) {
            throw new IllegalArgumentException("You can only respond to requests sent to you.");
        }

        connection.setStatus(status);
        return DTOMappings.INSTANCE.toFriendConnectionDTO(connectionRepository.save(connection));
    }

    public List<FriendConnection> getFriends() {
        User user = getLoggedInUser();
        List<FriendConnection> friends = connectionRepository.findByUserAndStatus(user, FriendConnectionStatus.ACCEPTED);
        friends.addAll(connectionRepository.findByFriendAndStatus(user, FriendConnectionStatus.ACCEPTED));
        return friends;
    }

    public List<FriendConnectionDTO> getFriendsWithDTO() {
        return getFriends().stream().map(DTOMappings.INSTANCE::toFriendConnectionDTO)
                .toList();
    }

    public List<FriendConnectionDTO> getPendingRequests() {
        User user = getLoggedInUser();
        return connectionRepository.findByFriendAndStatus(user, FriendConnectionStatus.PENDING)
                .stream().map(DTOMappings.INSTANCE::toFriendConnectionDTO)
                .toList();
    }

    public Integer countFriends() {
        User user = getLoggedInUser();
        return connectionRepository.countByUserAndStatus(user, FriendConnectionStatus.ACCEPTED);
    }

    @Transactional
    public void unfriend(Long friendId) {
        User user = getLoggedInUser();
        User friend = userRepository.findById(friendId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        FriendConnection friendConnection = connectionRepository.findByUserAndFriendAndStatus(user, friend, FriendConnectionStatus.ACCEPTED);
        if (friendConnection != null) {
            connectionRepository.delete(friendConnection);
        } else {
            throw new EntityNotFoundException("Friend connection not found");
        }
    }
}
