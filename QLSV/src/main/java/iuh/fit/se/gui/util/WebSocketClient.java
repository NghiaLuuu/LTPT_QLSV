package iuh.fit.se.gui.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * WebSocket Client để nhận real-time updates từ backend
 */
public class WebSocketClient {

    private static final String WS_URL = "http://localhost:8080/ws";
    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private boolean connected = false;

    public WebSocketClient() {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);

        stompClient = new WebSocketStompClient(sockJsClient);

        // Cấu hình ObjectMapper với JavaTimeModule để hỗ trợ LocalDate, LocalDateTime...
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setObjectMapper(objectMapper);

        stompClient.setMessageConverter(messageConverter);
    }

    /**
     * Kết nối đến WebSocket server
     */
    public void connect(Runnable onConnectCallback) {
        try {
            stompClient.connectAsync(WS_URL, new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    stompSession = session;
                    connected = true;
                    System.out.println("WebSocket connected!");
                    if (onConnectCallback != null) {
                        onConnectCallback.run();
                    }
                }

                @Override
                public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                    System.err.println("WebSocket error: " + exception.getMessage());
                    exception.printStackTrace();
                }

                @Override
                public void handleTransportError(StompSession session, Throwable exception) {
                    System.err.println("WebSocket transport error: " + exception.getMessage());
                    connected = false;
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to connect WebSocket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Subscribe vào một topic để nhận messages
     */
    public <T> void subscribe(String destination, Class<T> messageType, Consumer<T> messageHandler) {
        if (!connected || stompSession == null) {
            System.err.println("WebSocket not connected. Cannot subscribe to " + destination);
            return;
        }

        try {
            stompSession.subscribe(destination, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return messageType;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    if (payload != null && messageType.isInstance(payload)) {
                        messageHandler.accept(messageType.cast(payload));
                    }
                }
            });
            System.out.println("Subscribed to: " + destination);
        } catch (Exception e) {
            System.err.println("Failed to subscribe to " + destination + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Ngắt kết nối WebSocket
     */
    public void disconnect() {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
            connected = false;
            System.out.println("WebSocket disconnected");
        }
    }

    /**
     * Kiểm tra trạng thái kết nối
     */
    public boolean isConnected() {
        return connected && stompSession != null && stompSession.isConnected();
    }
}
