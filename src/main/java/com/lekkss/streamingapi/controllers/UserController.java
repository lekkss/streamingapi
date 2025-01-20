package com.lekkss.streamingapi.controllers;

import com.lekkss.streamingapi.services.UserService;
import com.lekkss.streamingapi.utils.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user")
@CrossOrigin("*")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Response<?>> getUser() {
        return ResponseEntity.ok(userService.getUser());
    }
}
