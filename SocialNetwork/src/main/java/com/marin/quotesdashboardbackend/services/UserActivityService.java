package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.dtos.DTOMappings;
import com.marin.quotesdashboardbackend.dtos.UserActivityDTO;
import com.marin.quotesdashboardbackend.entities.Post;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.entities.UserPostInteraction;
import com.marin.quotesdashboardbackend.repositories.PostRepository;
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
    private final PostRepository postRepository;
    private final UserPostInteractionRepository userPostInteractionRepository;

    private User getLoggedInUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    }

    public List<UserActivityDTO> getUserActivityFeed() {
        User user = getLoggedInUser();

        // Get friends list
        List<User> friends = friendConnectionService.getFriends().stream()
                .map(fc -> fc.getFriend().equals(user) ? fc.getUser() : fc.getFriend())
                .collect(Collectors.toList());

        friends.add(user);

        // Get latest public posts and friends' posts
        List<Post> posts = postRepository.findLatestPublicAndFriendsPosts(friends.stream().map(User::getId).toList());

        // Get friends' activities on public posts or their friends' activities
        List<UserPostInteraction> interactions = interactionRepository.findFriendsInteractionsOnPublicPosts(friends);

        // Combine posts and interactions
        List<UserActivityDTO> activityFeed = posts.stream()
                .map(DTOMappings::fromPostToUserActivityDTO)
                .collect(Collectors.toList());

        activityFeed.addAll(interactions.stream()
                .map(DTOMappings::fromUserInteractionToUserActivityDTO)
                .toList());

        // Sort by date
        activityFeed.sort((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()));

        return activityFeed;
    }
}
