package iuh.fit.se.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String role;
    private String refreshToken;

    public JwtResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public JwtResponse(String token, String refreshToken, String username, String role) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.username = username;
        this.role = role;
    }
}
