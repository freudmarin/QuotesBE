package com.marin.quotesdashboardbackend.controllers;

import com.marin.quotesdashboardbackend.dtos.JwtLoginRequest;
import com.marin.quotesdashboardbackend.response.JwtResponse;
import com.marin.quotesdashboardbackend.dtos.JwtSignupRequest;
import com.marin.quotesdashboardbackend.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<JwtResponse> registerUser(@RequestBody JwtSignupRequest signupRequest) {
        return ResponseEntity.ok(authenticationService.registerUser(signupRequest));
    }

    @PostMapping("authenticate")
    public ResponseEntity<JwtResponse> createAuthenticationToken(@RequestBody JwtLoginRequest authenticationRequest) {
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
