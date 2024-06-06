package com.marin.socialnetwork.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_post_interactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserPostInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    private boolean liked;
    private boolean shared;

    @Column(name = "liked_at")
    private LocalDateTime addedAt;
    @Column(name = "unliked_at")
    private LocalDateTime updatedAt;

    public UserPostInteraction(User user, boolean liked, Post post, boolean shared) {
        this.user = user;
        this.liked = liked;
        this.post = post;
        this.shared = shared;
    }
}
