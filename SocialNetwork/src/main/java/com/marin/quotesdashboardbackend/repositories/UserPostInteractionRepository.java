package com.marin.quotesdashboardbackend.repositories;

import com.marin.quotesdashboardbackend.entities.Post;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.entities.UserPostInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserPostInteractionRepository extends JpaRepository<UserPostInteraction, Long> {
    Optional<UserPostInteraction> findByUserAndPost(User user, Post post);

    int countByPostAndLikedTrue(Post post);


    @Query("SELECT up.post FROM UserPostInteraction up WHERE up.user = :user AND up.liked = true")
    List<Post> findPostsByUserAndLikedTrue(@Param("user") User user);

    @Query("SELECT up FROM UserPostInteraction up WHERE up.user = :user AND up.liked = true")
    List<UserPostInteraction> findByUserAndLikedTrue(@Param("user") User user);

    @Query("SELECT up FROM UserPostInteraction up JOIN FETCH up.post WHERE up.liked = true")
    List<UserPostInteraction> findAllLikedPosts();

    @Query("SELECT i FROM UserPostInteraction i " +
            "JOIN i.post p " +
            "WHERE (i.user IN :friends AND p.isPublic = true) " +
            "OR (p.user IN :friends AND p.isPublic = true) " +
            "ORDER BY i.addedAt DESC")
    List<UserPostInteraction> findFriendsInteractionsOnPublicPosts(@Param("friends") List<User> friends);
}
