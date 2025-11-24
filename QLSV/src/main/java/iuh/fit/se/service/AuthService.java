package iuh.fit.se.service;

import iuh.fit.se.dto.request.LoginRequest;
import iuh.fit.se.dto.request.RegisterRequest;
import iuh.fit.se.dto.request.ChangePasswordRequest;
import iuh.fit.se.dto.response.JwtResponse;

public interface AuthService {
    JwtResponse login(LoginRequest request);
    String register(RegisterRequest request);
    void changePassword(String username, ChangePasswordRequest request);
    JwtResponse refreshToken(String refreshToken);
    void logout(String username);
}
