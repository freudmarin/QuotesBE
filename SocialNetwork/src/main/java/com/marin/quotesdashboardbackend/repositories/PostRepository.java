package com.marin.quotesdashboardbackend.repositories;

import com.marin.quotesdashboardbackend.entities.Post;
import com.marin.quotesdashboardbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorAndDeletedIsFalse(User author);

    @Query("SELECT p FROM Post p WHERE p.author.id = :authorId AND p.isDeleted = false AND (p.isPublic = true OR p.author IN (SELECT fc.friend FROM FriendConnection fc WHERE fc.user = :requestingUser AND fc.status = 'ACCEPTED'))")
    List<Post> findAccessiblePosts(@Param("authorId") Long authorId, @Param("requestingUser") User requestingUser);
}
