package com.marin.socialnetwork.entities;

import com.marin.socialnetwork.enums.FriendConnectionStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "friend_connections")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class FriendConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User friend;

    @Enumerated(EnumType.STRING)
    private FriendConnectionStatus status;
}
