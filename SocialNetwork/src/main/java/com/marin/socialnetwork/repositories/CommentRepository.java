package com.marin.socialnetwork.repositories;

import com.marin.socialnetwork.entities.Comment;
import com.marin.socialnetwork.entities.Post;
import com.marin.socialnetwork.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost(Post post);

    Optional<Comment> findByIdAndUser(Long id, User user);
}
