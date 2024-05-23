package com.marin.quotesdashboardbackend.entities;

import com.marin.quotesdashboardbackend.enums.FriendConnectionStatus;
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

    private FriendConnectionStatus status;
}
