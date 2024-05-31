package com.marin.quotesdashboardbackend.dtos;

import com.marin.quotesdashboardbackend.enums.FriendConnectionStatus;
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
