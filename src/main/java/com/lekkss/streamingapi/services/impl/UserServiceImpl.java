package com.lekkss.streamingapi.services.impl;

import com.lekkss.streamingapi.DTO.UserDTO;
import com.lekkss.streamingapi.repositories.UserRepository;
import com.lekkss.streamingapi.services.UserService;
import com.lekkss.streamingapi.utils.Response;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Response<?> getUser() {
        // Get the authenticated user's details
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return new Response<>(false, "User is not authenticated", null);
        }
        // Extract the user details from the authentication token
        var userEmail = authentication.getName();
        // Find the user in the database
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // Map user to a DTO to avoid exposing sensitive information
        var userDTO = new UserDTO(user);
        // Return the response with user details
        return new Response<>(true, "User retrieved successfully", userDTO);
    }

}
