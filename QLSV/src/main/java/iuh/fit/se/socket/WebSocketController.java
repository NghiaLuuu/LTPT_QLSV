package iuh.fit.se.socket;

import iuh.fit.se.model.Notification;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/notification")
    @SendTo("/topic/notifications")
    public Notification sendNotification(Notification notification) {
        return notification;
    }
}
