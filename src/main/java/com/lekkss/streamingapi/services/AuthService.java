package com.lekkss.streamingapi.services;

import com.lekkss.streamingapi.models.User;
import com.lekkss.streamingapi.utils.AuthResponse;
import com.lekkss.streamingapi.utils.LoginRequest;
import com.lekkss.streamingapi.utils.RegisterRequest;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse register(RegisterRequest registerRequest);
}
