package com.marin.quotesdashboardbackend.repositories;

import com.marin.quotesdashboardbackend.entities.Comment;
import com.marin.quotesdashboardbackend.entities.Post;
import com.marin.quotesdashboardbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost(Post post);

    Optional<Comment> findByIdAndUser(Long id, User user);
}
