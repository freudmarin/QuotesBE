package com.marin.quotesdashboardbackend.controllers;

import com.marin.quotesdashboardbackend.dtos.FriendConnectionDTO;
import com.marin.quotesdashboardbackend.enums.FriendConnectionStatus;
import com.marin.quotesdashboardbackend.services.FriendConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/friends")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class FriendConnectionController {

    private final FriendConnectionService connectionService;

    @PostMapping("request")
    public ResponseEntity<FriendConnectionDTO> sendFriendRequest(@RequestParam Long friendId) {
        return ResponseEntity.ok(connectionService.sendFriendRequest(friendId));
    }

    @PostMapping("accept")
    public ResponseEntity<FriendConnectionDTO> acceptFriendRequest(@RequestParam Long connectionId) {
        return ResponseEntity.ok(connectionService.respondToFriendRequest(connectionId, FriendConnectionStatus.ACCEPTED));
    }

    @PostMapping("refuse")
    public ResponseEntity<FriendConnectionDTO> refuseFriendRequest(@RequestParam Long connectionId) {
        return ResponseEntity.ok(connectionService.respondToFriendRequest(connectionId, FriendConnectionStatus.REJECTED));
    }

    @GetMapping("list")
    public ResponseEntity<List<FriendConnectionDTO>> getFriends() {
        return ResponseEntity.ok(connectionService.getFriendsWithDTO());
    }

    @GetMapping("requests")
    public ResponseEntity<List<FriendConnectionDTO>> getPendingRequests() {
        return ResponseEntity.ok(connectionService.getPendingRequests());
    }

    @GetMapping("friends-count")
    public ResponseEntity<Integer> getFriendsCount() {
        return ResponseEntity.ok(connectionService.countFriends());
    }

    @DeleteMapping("unfriend/{friendId}")
    public ResponseEntity<Void> unfriend(@PathVariable("friendId") Long friendId) {
        connectionService.unfriend(friendId);
        return ResponseEntity.noContent().build();
    }
}
