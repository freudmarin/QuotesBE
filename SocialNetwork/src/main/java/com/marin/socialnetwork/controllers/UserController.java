package com.marin.socialnetwork.controllers;


import com.marin.socialnetwork.dtos.UserProfileDTO;
import com.marin.socialnetwork.dtos.UserProfileUpdateDTO;
import com.marin.socialnetwork.entities.User;
import com.marin.socialnetwork.services.CustomUserDetailsService;
import com.marin.socialnetwork.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/users")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final CustomUserDetailsService userDetailsService;

    private User getLoggedInUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetailsService.loadUserEntityByUsername(userDetails.getUsername());
    }

    @GetMapping("{userId}/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long userId) {
        User requestingUser = getLoggedInUser();
        UserProfileDTO userProfile = userService.getUserProfile(userId, requestingUser);
        return ResponseEntity.ok(userProfile);
    }

    @PostMapping("profile")
    public ResponseEntity<Void> updateUserProfile(@RequestBody UserProfileUpdateDTO updateDTO) {
        User user = getLoggedInUser();
        userService.updateUserProfile(user, updateDTO);
       return ResponseEntity.ok().build();
    }

    @PostMapping("profile/picture")
    public ResponseEntity<String> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        User user = getLoggedInUser();
        String profilePictureUrl = userService.uploadProfilePicture(user, file);
        return ResponseEntity.ok(profilePictureUrl);
    }
}
