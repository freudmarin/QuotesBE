package com.marin.quotesdashboardbackend.dtos;

import com.marin.quotesdashboardbackend.entities.*;

import java.util.List;
import java.util.stream.Collectors;

public class DTOMappings {

    public static AuthorDTO fromAuthorToAuthorDTO(Author author) {
        return new AuthorDTO(author.getId(), author.getName(), author.getBio());
    }

    public static CommentDTO fromCommentToCommentDTO(Comment comment) {
        return new CommentDTO(comment.getId(), comment.getContent());
    }

    public static UserDTO fromUserToUserDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getUsername(), user.getBio());
    }

    public static FriendConnectionDTO fromFriendConnectionToFriendConnectionDTO(FriendConnection connection) {
        return new FriendConnectionDTO(connection.getId(), fromUserToUserDTO(connection.getUser()), fromUserToUserDTO(connection.getFriend()), connection.getStatus());
    }

    public static PostDTO fromPostToPostDTO(Post post) {
        return new PostDTO(post.getId(), fromQuoteToQuoteDTO(post.getQuote()), post.getComments() == null ? null : post.getComments().stream().map(DTOMappings::fromCommentToCommentDTO).toList(),
                post.getPostPhotoUrl(), fromUserToUserDTO(post.getUser()), post.getText(), post.isPublic(), post.getCreatedAt(), post.getUpdatedAt());
    }

    public static QuoteDTO fromQuoteToQuoteDTO(Quote quote) {
        return new QuoteDTO(quote.getId(), quote.getText(), fromAuthorToAuthorDTO(quote.getAuthor()), quote.getTags().stream().map(DTOMappings::fromTagToTagDTO).toList());
    }

    public static UserPostInteractionDTO fromInteractionToUserPostInteractionDTO(UserPostInteraction interaction) {
        return new UserPostInteractionDTO(interaction.getId(), fromUserToUserDTO(interaction.getUser()), interaction.isLiked(), interaction.isShared(), interaction.getAddedAt(), interaction.getUpdatedAt());
    }

    public static UserActivityDTO fromPostToUserActivityDTO(Post post) {
        List<UserPostInteractionDTO> interactions = post.getInteractions().stream()
                .map(DTOMappings::fromInteractionToUserPostInteractionDTO)
                .collect(Collectors.toList());

        return new UserActivityDTO(fromUserToUserDTO(post.getUser()), fromPostToPostDTO(post), interactions, post.getCreatedAt(), post.getUpdatedAt());
    }

    public static UserActivityDTO fromUserInteractionToUserActivityDTO(UserPostInteraction userInteraction) {
        return new UserActivityDTO(fromUserToUserDTO(userInteraction.getUser()), fromPostToPostDTO(userInteraction.getPost()),
                userInteraction.getPost().getInteractions().stream().map(DTOMappings::fromInteractionToUserPostInteractionDTO).toList(), userInteraction.getAddedAt(), userInteraction.getUpdatedAt());
    }


    public static UserProfileDTO fromUserToProfileDTO(User user, List<PostDTO> postsDTO) {
        return new UserProfileDTO(user.getId(), user.getEmail(), user.getName(),
                user.getProfilePictureUrl(), user.getBio(), user.getSocialLinks(),
                user.getFavoriteTags().stream().map(DTOMappings::fromTagToTagDTO).collect(Collectors.toSet()),
                user.getFavoriteAuthors().stream().map(DTOMappings::fromAuthorToAuthorDTO).collect(Collectors.toSet()),
                postsDTO);
    }

    public static TagDTO fromTagToTagDTO(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        return dto;
    }
}
