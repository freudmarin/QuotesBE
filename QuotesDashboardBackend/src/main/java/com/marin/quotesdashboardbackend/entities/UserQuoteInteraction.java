package com.marin.quotesdashboardbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "user_quote_interactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserQuoteInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "quote_id")
    private Quote quote;

    private boolean liked;
    private boolean shared;

    public UserQuoteInteraction(User user, boolean liked, Quote quote, boolean shared) {
        this.user = user;
        this.liked = liked;
        this.quote = quote;
        this.shared = shared;
    }

    // Getters and setters
}
