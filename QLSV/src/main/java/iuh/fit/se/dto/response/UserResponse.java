package iuh.fit.se.dto.response;

import iuh.fit.se.model.Role;
import iuh.fit.se.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private Role role;
    private Boolean active;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.active = user.getActive();
    }
}
