package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.dtos.DTOMappings;
import com.marin.quotesdashboardbackend.dtos.UserActivityDTO;
import com.marin.quotesdashboardbackend.dtos.UserPostKey;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserActivityService {

    private final UserPostInteractionRepository interactionRepository;
    private final FriendConnectionService friendConnectionService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

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
        List<Post> friendAndPublicPosts = postRepository.findLatestPublicAndFriendsPosts(friends.stream().map(User::getId).toList());

        // Get friends' activities on public posts or their friends' activities
        List<UserPostInteraction> friendInteractions = interactionRepository.findFriendsInteractionsOnPublicPosts(friends);

        // Use a map to ensure uniqueness based on a custom key of user ID and post ID
        Map<UserPostKey, UserActivityDTO> activityFeedMap = new HashMap<>();

        friendAndPublicPosts.stream()
                .map(DTOMappings::fromPostToUserActivityDTO)
                .forEach(dto -> {
                    UserPostKey key = new UserPostKey(dto.getUser().getId(), dto.getPost().getId());
                    System.out.println("Adding post activity with key: " + key);
                    activityFeedMap.putIfAbsent(key, dto);
                });

        friendInteractions.stream()
                .map(DTOMappings::fromUserInteractionToUserActivityDTO)
                .forEach(dto -> {
                    UserPostKey key = new UserPostKey(dto.getUser().getId(), dto.getPost().getId());
                    System.out.println("Adding interaction activity with key: " + key);
                    activityFeedMap.merge(key, dto, (existing, newDto) -> {
                        existing.getInteractions().addAll(newDto.getInteractions());
                        return existing;
                    });
                });

        // Convert the map values to a list and sort by date
        List<UserActivityDTO> activityFeed = new ArrayList<>(activityFeedMap.values());
        activityFeed.sort((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()));

        return activityFeed;
    }
}
