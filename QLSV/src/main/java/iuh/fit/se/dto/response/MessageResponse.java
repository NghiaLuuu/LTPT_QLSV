package iuh.fit.se.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String message;
    private Object data;

    public MessageResponse(String message) {
        this.message = message;
    }
}
