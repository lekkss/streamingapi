package com.lekkss.streamingapi.controllers;

import com.lekkss.streamingapi.services.AuthService;
import com.lekkss.streamingapi.utils.AuthResponse;
import com.lekkss.streamingapi.utils.LoginRequest;
import com.lekkss.streamingapi.utils.RegisterRequest;
import com.lekkss.streamingapi.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
public class AuthenticationController {

    @Autowired
    private AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<Response<?>> login(@RequestBody LoginRequest loginRequest) {
        Response<?> authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(
                authResponse
        );
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        AuthResponse authResponse = authService.register(registerRequest);
        return ResponseEntity.ok(
               authResponse
        );
    }

}
