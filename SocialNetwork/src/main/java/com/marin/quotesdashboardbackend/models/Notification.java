package com.marin.quotesdashboardbackend.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notification {

    private String type;
    private String message;
    private Long postId;

}
