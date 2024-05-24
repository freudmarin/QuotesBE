package com.marin.quotesdashboardbackend.repositories;

import com.marin.quotesdashboardbackend.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
