package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.dtos.DTOMappings;
import com.marin.quotesdashboardbackend.dtos.UserActivityDTO;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.repositories.UserPostInteractionRepository;
import com.marin.quotesdashboardbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserActivityService {

    private final UserPostInteractionRepository interactionRepository;
    private final FriendConnectionService friendConnectionService;
    private final UserRepository userRepository;

    private User getLoggedInUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    }

    /*could be improved*/
    public List<UserActivityDTO> getUserActivityFeed() {
        User user = getLoggedInUser();
        List<User> friends = friendConnectionService.getFriends().stream()
                .map(fc -> fc.getFriend().equals(user) ? fc.getUser() : fc.getFriend())
                .collect(Collectors.toList());
        friends.add(user);

        return interactionRepository.findByUserInOrderByAddedAtDesc(friends).stream().map(
                DTOMappings::fromUserInteractionToUserActivityDTO
        ).toList();
    }
}
