package com.marin.quotesdashboardbackend.controller;


import com.marin.quotesdashboardbackend.dtos.UserProfileDTO;
import com.marin.quotesdashboardbackend.dtos.UserProfileUpdateDTO;
import com.marin.quotesdashboardbackend.entities.User;
import com.marin.quotesdashboardbackend.services.CustomUserDetailsService;
import com.marin.quotesdashboardbackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/users")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final CustomUserDetailsService userDetailsService;

    @GetMapping("profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        User user = userDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        UserProfileDTO profile = userService.getUserProfile(user);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("profile")
    public ResponseEntity<UserProfileDTO> updateUserProfile(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
            @RequestBody UserProfileUpdateDTO updateDTO) {
        User user = userDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        UserProfileDTO profile = userService.updateUserProfile(user, updateDTO);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("profile/picture")
    public ResponseEntity<String> uploadProfilePicture(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
            @RequestParam("file") MultipartFile file) {
        User user = userDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        String profilePictureUrl = userService.uploadProfilePicture(user, file);
        return ResponseEntity.ok(profilePictureUrl);
    }
}
