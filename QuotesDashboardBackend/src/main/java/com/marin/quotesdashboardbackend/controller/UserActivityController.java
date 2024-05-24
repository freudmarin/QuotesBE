package com.marin.quotesdashboardbackend.controller;

import com.marin.quotesdashboardbackend.dtos.UserActivityDTO;
import com.marin.quotesdashboardbackend.services.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/activity")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class UserActivityController {

    private final UserActivityService activityService;

    @GetMapping("feed")
    public ResponseEntity<List<UserActivityDTO>> getUserActivityFeed() {
        return ResponseEntity.ok(activityService.getUserActivityFeed());
    }
}