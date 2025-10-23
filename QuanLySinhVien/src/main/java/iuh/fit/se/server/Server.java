package iuh.fit.se.server;

import iuh.fit.se.server.net.ClientHandler;
import iuh.fit.se.server.service.IStudentService;
import iuh.fit.se.server.service.JpaStudentServiceAdapter;
import iuh.fit.se.server.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 8888; // ĐỔI PORT từ 8080 → 8888
    private static final int THREAD_POOL_SIZE = 10;
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    // Flag để bỏ qua JPA (set = true nếu không có SQL Server)
    private static final boolean SKIP_JPA = true; // ĐẶT true để dùng in-memory mode ngay

    private static ExecutorService executorService;

    public static void main(String[] args) {
        logger.info("Starting server...");

        IStudentService studentService;
        EntityManagerFactory emf = null;

        if (SKIP_JPA) {
            logger.info("JPA disabled (SKIP_JPA=true), using in-memory mode");
            studentService = new StudentService();
        } else {
            try {
                // Try to create EMF (will fail if JPA deps or configuration missing)
                emf = Persistence.createEntityManagerFactory("StudentPU_SQLServer");
                studentService = new JpaStudentServiceAdapter(emf);
                logger.info("Using JPA-backed StudentService");
            } catch (Throwable t) {
                logger.warn("Cannot initialize JPA EntityManagerFactory (falling back to in-memory). Reason: {}", t.getMessage());
                studentService = new StudentService();
            }
        }

        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        final EntityManagerFactory finalEmf = emf;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down server...");
            if (executorService != null) executorService.shutdown();
            if (finalEmf != null && finalEmf.isOpen()) {
                finalEmf.close();
                logger.info("Closed EntityManagerFactory");
            }
            logger.info("Server stopped.");
        }));

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Server listening on port {}", PORT);
            //noinspection InfiniteLoopStatement
            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("New client connected: {}", clientSocket.getInetAddress());
                executorService.submit(new ClientHandler(clientSocket, studentService));
            }
        } catch (Exception e) {
            logger.error("Server error: {}", e.getMessage(), e);
        }
    }
}
