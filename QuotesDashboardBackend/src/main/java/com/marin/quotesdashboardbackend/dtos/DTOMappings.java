package com.marin.quotesdashboardbackend.dtos;

import com.marin.quotesdashboardbackend.entities.*;

import java.util.stream.Collectors;

public class DTOMappings {

    public static  QuoteDTO fromQuoteToQuoteDTO(Quote quote) {
        return new QuoteDTO(quote.getId(), quote.getText(), fromAuthorToAuthorDTO(quote.getAuthor()));
    }

    public static  AuthorDTO fromAuthorToAuthorDTO(Author author) {
        return new AuthorDTO(author.getId(), author.getName());
    }

    public static  CommentDTO fromCommentToCommentDTO(Comment comment) {
        return new CommentDTO(comment.getId(), comment.getContent());
    }

    public static UserDTO fromUserToUserDTO(User user) {
        return new UserDTO(user.getName(), user.getUsername());
    }

    public static FriendConnectionDTO fromFriendConnectionToFriendConnectionDTO(FriendConnection connection) {
        return new FriendConnectionDTO(connection.getId(), fromUserToUserDTO(connection.getUser()), fromUserToUserDTO(connection.getFriend()), connection.getStatus());
    }

    public static UserActivityDTO fromUserInteractionToUserActivityDTO(UserQuoteInteraction userInteraction) {
        return new UserActivityDTO(fromUserToUserDTO(userInteraction.getUser()), fromQuoteToQuoteDTO(userInteraction.getQuote()),
                userInteraction.isLiked(), userInteraction.getAddedAt(), userInteraction.getUpdatedAt());
    }

    public static UserProfileDTO fromUserToProfileDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setBio(user.getBio());
        dto.setSocialLinks(user.getSocialLinks());
        dto.setFavoriteTags(user.getFavoriteTags().stream().map(DTOMappings::fromTagToTagDTO).collect(Collectors.toSet()));
        dto.setFavoriteAuthors(user.getFavoriteAuthors().stream().map(DTOMappings::fromAuthorToAuthorDTO).collect(Collectors.toSet()));
        return dto;
    }

    public static TagDTO fromTagToTagDTO(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        return dto;
    }
}
