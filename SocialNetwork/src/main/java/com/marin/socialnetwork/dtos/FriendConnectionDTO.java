package com.marin.socialnetwork.dtos;

import com.marin.socialnetwork.enums.FriendConnectionStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class FriendConnectionDTO {

    private Long id;

    private UserDTO user;


    private UserDTO friend;

    private FriendConnectionStatus status;

}
