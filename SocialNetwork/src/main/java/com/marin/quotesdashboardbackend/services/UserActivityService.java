package com.marin.quotesdashboardbackend.services;

import com.marin.quotesdashboardbackend.dtos.DTOMappings;
import com.marin.quotesdashboardbackend.dtos.UserActivityDTO;
import com.marin.quotesdashboardbackend.dtos.UserPostInteractionDTO;
import com.marin.quotesdashboardbackend.entities.Post;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.entities.UserPostInteraction;
import com.marin.quotesdashboardbackend.repositories.PostRepository;
import com.marin.quotesdashboardbackend.repositories.UserPostInteractionRepository;
import com.marin.quotesdashboardbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserActivityService {

    private final UserPostInteractionRepository interactionRepository;
    private final FriendConnectionService friendConnectionService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public List<UserActivityDTO> getUserActivityFeed(int page, int size) {
        User user = getLoggedInUser();
        List<User> friends = getFriends(user);
        friends.add(user);

        Pageable pageable = PageRequest.of(page, size);
        log.info("Fetching posts for friends and public with pageable: {}", pageable);
        Page<Post> friendAndPublicPostsPage = getFriendAndPublicPosts(friends, pageable);
        List<Post> friendAndPublicPosts = friendAndPublicPostsPage.getContent();
        List<UserPostInteraction> friendInteractions = getFriendInteractions(friends);

        log.info("Creating activity feed");
        List<UserActivityDTO> activityFeed = createActivityFeed(friendAndPublicPosts, friendInteractions);

        activityFeed.sort((a1, a2) -> a2.getAddedAt().compareTo(a1.getAddedAt()));

        return activityFeed;
    }

    private User getLoggedInUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private List<User> getFriends(User user) {
        return friendConnectionService.getFriends().stream()
                .map(fc -> fc.getFriend().equals(user) ? fc.getUser() : fc.getFriend())
                .collect(Collectors.toList());
    }

    private Page<Post> getFriendAndPublicPosts(List<User> friends, Pageable pageable) {
        return postRepository.findLatestPublicAndFriendsPosts(friends.stream().map(User::getId).toList(), pageable);
    }

    private List<UserPostInteraction> getFriendInteractions(List<User> friends) {
        return interactionRepository.findFriendsInteractionsOnPublicPosts(friends);
    }

    private List<UserActivityDTO> createActivityFeed(List<Post> posts, List<UserPostInteraction> interactions) {
        List<UserActivityDTO> activityFeed = new ArrayList<>();

        posts.forEach(post -> {
            UserActivityDTO userActivityDTO = DTOMappings.INSTANCE.toUserActivityDTO(post);
            userActivityDTO.setInteractions(mapInteractions(post.getInteractions()));
            activityFeed.add(userActivityDTO);
        });

        interactions.forEach(interaction -> {
            UserActivityDTO activityDTO = DTOMappings.INSTANCE.toUserActivityDTO(interaction);
            activityDTO.setInteractions(List.of(DTOMappings.INSTANCE.toUserPostInteractionDTO(interaction)));
            activityFeed.add(activityDTO);
        });

        return activityFeed;
    }

    private List<UserPostInteractionDTO> mapInteractions(List<UserPostInteraction> interactions) {
        if (interactions == null) {
            return new ArrayList<>();
        }
        return interactions.stream()
                .map(DTOMappings.INSTANCE::toUserPostInteractionDTO)
                .collect(Collectors.toList());
    }
}
