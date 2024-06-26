package com.marin.socialnetwork.repositories;

import com.marin.socialnetwork.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String userName);
}
