package com.marin.quotesdashboardbackend.repositories;

import com.marin.quotesdashboardbackend.entities.Quote;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.entities.UserQuoteInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserQuoteInteractionRepository extends JpaRepository<UserQuoteInteraction, Long> {
    Optional<UserQuoteInteraction> findByUserAndQuote(User user, Quote quote);

    int countByQuoteAndLikedTrue(Quote quote);


    @Query("SELECT distinct ui.user FROM UserQuoteInteraction ui WHERE ui.quote IN " +
            "(SELECT uii.quote FROM UserQuoteInteraction uii WHERE uii.user.id = :userId AND uii.liked = true) " +
            "AND ui.user.id != :userId AND ui.liked = true")
    List<User> findUsersWithSimilarInterests(Long userId);


    @Query("SELECT u FROM UserQuoteInteraction ui JOIN ui.user u WHERE ui.quote = :quote AND ui.liked = true")
    List<User> findUsersByQuoteAndLikedTrue(@Param("quote") Quote quote);

    List<UserQuoteInteraction> findByUserAndLikedTrue(User user);

    @Query("SELECT ui.quote FROM UserQuoteInteraction ui WHERE ui.user = :user AND ui.liked = true")
    List<Quote> findQuotesByUserAndLikedTrue(@Param("user") User user);

}
