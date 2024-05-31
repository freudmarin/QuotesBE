package com.marin.quotesdashboardbackend.dtos;

import com.marin.quotesdashboardbackend.entities.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(componentModel = "spring")
public interface DTOMappings {
    DTOMappings INSTANCE = Mappers.getMapper(DTOMappings.class);

    TagDTO toTagDTO(Tag tag);
    AuthorDTO toAuthorDTO(Author author);

    CommentDTO toCommentDTO(Comment comment);

    PostDTO toPostDTO(Post post);

    UserDTO toUserDTO(User user);
    UserProfileDTO toUserProfileDTO(User user, List<PostDTO> posts);
    FriendConnectionDTO toFriendConnectionDTO(FriendConnection friendConnection);
    UserPostInteractionDTO toUserPostInteractionDTO(UserPostInteraction interaction);

    @Mapping(target = "user", source = "user")
    @Mapping(target = "post", expression = "java(toPostDTO(post))")
    @Mapping(target = "addedAt", source = "addedAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "interactions", ignore = true)
    UserActivityDTO toUserActivityDTO(Post post);

    @Mapping(target = "user", source = "user")
    @Mapping(target = "addedAt", source = "addedAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "post", source = "post")
    @Mapping(target = "interactions", ignore = true)
    UserActivityDTO toUserActivityDTO(UserPostInteraction interaction);

    @Mapping(target = "tags", source = "tags")
    QuoteDTO toQuoteDTO(Quote quote);
}
