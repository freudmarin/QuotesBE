package com.marin.socialnetwork.controllers;

import com.marin.socialnetwork.dtos.JwtLoginRequest;
import com.marin.socialnetwork.dtos.JwtSignupRequest;
import com.marin.socialnetwork.response.JwtResponse;
import com.marin.socialnetwork.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("register")
    public ResponseEntity<JwtResponse> registerUser(@Valid @RequestBody JwtSignupRequest signupRequest) {
        return ResponseEntity.ok(authenticationService.registerUser(signupRequest));
    }

    @PostMapping("authenticate")
    public ResponseEntity<JwtResponse> createAuthenticationToken(@Valid @RequestBody JwtLoginRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticateUser(authenticationRequest));
    }


    @PostMapping("set-password")
    public ResponseEntity<?> setPassword(@RequestParam String email, @RequestParam String password) {
        authenticationService.setPassword(email, password);
        return ResponseEntity.ok("Password set successfully");
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication,
                                       @RequestHeader("Authorization") String token) {
        authenticationService.logout(request, response, authentication, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
