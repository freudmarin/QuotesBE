package com.marin.quotesdashboardbackend.repositories;

import com.marin.quotesdashboardbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String userName);
}
