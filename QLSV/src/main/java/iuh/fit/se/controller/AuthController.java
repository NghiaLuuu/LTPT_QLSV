package iuh.fit.se.controller;

import iuh.fit.se.dto.request.LoginRequest;
import iuh.fit.se.dto.request.RegisterRequest;
import iuh.fit.se.dto.request.ChangePasswordRequest;
import iuh.fit.se.dto.request.TokenRefreshRequest;
import iuh.fit.se.dto.response.JwtResponse;
import iuh.fit.se.dto.response.MessageResponse;
import iuh.fit.se.exception.BadRequestException;
import iuh.fit.se.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            JwtResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (BadRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(ex.getMessage()));
        } catch (Exception ex) {
            // Log để debug
            System.err.println("Login error: " + ex.getClass().getName() + " - " + ex.getMessage());
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(ex.getMessage() != null ? ex.getMessage() : "Đăng nhập thất bại"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        String message = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse(message));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        JwtResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(Authentication authentication,
                                           @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok(new MessageResponse("Đổi mật khẩu thành công"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        if (authentication != null) {
            authService.logout(authentication.getName());
        }
        return ResponseEntity.ok(new MessageResponse("Đăng xuất thành công"));
    }
}
