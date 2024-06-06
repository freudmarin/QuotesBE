package com.marin.socialnetwork.controllers;

import com.marin.socialnetwork.dtos.UserActivityDTO;
import com.marin.socialnetwork.services.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/activity")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class UserActivityController {

    private final UserActivityService activityService;

    @GetMapping("feed")
    public ResponseEntity<List<UserActivityDTO>> getUserActivityFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(activityService.getUserActivityFeed(page, size));
    }
}
