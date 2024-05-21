package com.marin.quotesdashboardbackend.repositories;

import com.marin.quotesdashboardbackend.entities.Quote;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.entities.UserQuoteInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserQuoteInteractionRepository extends JpaRepository<UserQuoteInteraction, Long> {
    Optional<UserQuoteInteraction> findByUserAndQuote(User user, Quote quote);
}
