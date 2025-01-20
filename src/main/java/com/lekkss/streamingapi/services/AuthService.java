package com.lekkss.streamingapi.services;

import com.lekkss.streamingapi.utils.AuthResponse;
import com.lekkss.streamingapi.utils.LoginRequest;
import com.lekkss.streamingapi.utils.RegisterRequest;
import com.lekkss.streamingapi.utils.Response;

public interface AuthService {
    Response<?> login(LoginRequest loginRequest);
    AuthResponse register(RegisterRequest registerRequest);
}
