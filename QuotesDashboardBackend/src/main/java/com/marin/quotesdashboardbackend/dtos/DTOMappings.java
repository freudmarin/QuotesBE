package com.marin.quotesdashboardbackend.dtos;

import com.marin.quotesdashboardbackend.entities.*;

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
}
