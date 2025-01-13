package com.lekkss.streamingapi.services.impl;

import com.lekkss.streamingapi.config.JwtService;
import com.lekkss.streamingapi.models.Role;
import com.lekkss.streamingapi.models.User;
import com.lekkss.streamingapi.repositories.UserRepository;
import com.lekkss.streamingapi.services.AuthService;
import com.lekkss.streamingapi.utils.AuthResponse;
import com.lekkss.streamingapi.utils.LoginRequest;
import com.lekkss.streamingapi.utils.RegisterRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private  final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        // Authenticate the user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        // Find the user by email
        var user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Generate a JWT token
        var token = jwtService.generateToken(user);
        // Return the response
        return new AuthResponse(token,true, "User logged in successfully");
    }

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if the user already exists
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        // Map RegisterRequest to User entity
        var user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Hash the password
        user.setRole(Role.USER); // Default role

        // Save the user to the database
        userRepository.save(user);

        // Generate JWT token for the user
        var token = jwtService.generateToken(user);

        // Return response
        return new AuthResponse(token, true, "User registered successfully");
    }

}
