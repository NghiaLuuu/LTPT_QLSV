package iuh.fit.se.client.net;

import iuh.fit.se.common.protocol.Request;
import iuh.fit.se.common.protocol.Response;
import iuh.fit.se.common.protocol.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkClient implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(NetworkClient.class);

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public NetworkClient(String host, int port) throws Exception {
        connect(host, port);
    }

    public void connect(String host, int port) throws Exception {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush(); // QUAN TRỌNG: flush để gửi stream header trước
        in = new ObjectInputStream(socket.getInputStream());
        logger.info("Connected to server {}:{}", host, port);
    }

    public Response sendRequest(Request req) {
        try {
            out.writeObject(req);
            out.flush();
            Object res = in.readObject();
            if (res instanceof Response) return (Response) res;
            return new Response(Status.ERROR, "Invalid response from server", null);
        } catch (Exception e) {
            logger.error("Error sending request: {}", e.getMessage(), e);
            return new Response(Status.ERROR, "Mất kết nối đến server.", null);
        }
    }

    @Override
    public void close() {
        try { if (in != null) in.close(); } catch (Exception ignored) {}
        try { if (out != null) out.close(); } catch (Exception ignored) {}
        try { if (socket != null) socket.close(); } catch (Exception ignored) {}
        logger.info("NetworkClient closed");
    }
}
