package iuh.fit.se.server;

import iuh.fit.se.server.net.ClientHandler;
import iuh.fit.se.server.service.AuthService;
import iuh.fit.se.server.service.IStudentService;
import iuh.fit.se.server.service.JpaStudentServiceAdapter;
import iuh.fit.se.server.service.StudentService;
import iuh.fit.se.server.util.DataSeeder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 8888;
    private static final int THREAD_POOL_SIZE = 10;
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    // QUAN TRỌNG: Đặt true để bỏ qua SQL Server và dùng in-memory (để test nhanh)
    // Đặt false để sử dụng JPA với SQL Server
    private static final boolean SKIP_JPA = false; // Đổi thành false để kết nối SQL Server

    private static ExecutorService executorService;

    public static void main(String[] args) {
        logger.info("Starting server on port {}...", PORT);

        IStudentService studentService;
        EntityManagerFactory emf = null;

        if (SKIP_JPA) {
            logger.info("JPA disabled (SKIP_JPA=true), using in-memory mode");
            studentService = new StudentService();
        } else {
            try {
                // Khởi tạo JPA EntityManagerFactory
                logger.info("Initializing JPA with SQL Server...");
                emf = Persistence.createEntityManagerFactory("StudentPU_SQLServer");
                studentService = new JpaStudentServiceAdapter(emf);
                logger.info("✓ JPA initialized successfully with SQL Server");
                logger.info("✓ All Service singletons will be initialized on first use");

                // Khởi tạo tài khoản admin mặc định nếu chưa có
                try {
                    AuthService.getInstance().initializeDefaultAdmin();
                } catch (Exception e) {
                    logger.warn("Could not initialize default admin account: {}", e.getMessage());
                }

                // Seed dữ liệu mẫu (10 sinh viên, 10 giảng viên)
                try {
                    DataSeeder.seedAll(studentService);
                } catch (Exception e) {
                    logger.warn("Could not seed sample data: {}", e.getMessage());
                }
            } catch (Throwable t) {
                logger.error("✗ Cannot initialize JPA EntityManagerFactory!", t);
                logger.error("Please check:");
                logger.error("  1. SQL Server is running");
                logger.error("  2. Database 'db_quanlysinhvien' exists");
                logger.error("  3. Connection string in persistence.xml is correct");
                logger.error("  4. Username/Password are correct");
                logger.error("\nFalling back to in-memory mode...");
                studentService = new StudentService();
            }
        }

        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        final EntityManagerFactory finalEmf = emf;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down server...");
            if (executorService != null) {
                executorService.shutdown();
                logger.info("Thread pool stopped");
            }
            if (finalEmf != null && finalEmf.isOpen()) {
                finalEmf.close();
                logger.info("EntityManagerFactory closed");
            }
            logger.info("Server stopped.");
        }));

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("========================================");
            logger.info("✓ Server started successfully!");
            logger.info("✓ Listening on port: {}", PORT);
            logger.info("✓ Thread pool size: {}", THREAD_POOL_SIZE);
            logger.info("✓ JPA mode: {}", SKIP_JPA ? "DISABLED (in-memory)" : "ENABLED (SQL Server)");
            logger.info("========================================");
            logger.info("Waiting for clients...");

            //noinspection InfiniteLoopStatement
            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("→ New client connected: {} (Port: {})",
                    clientSocket.getInetAddress().getHostAddress(),
                    clientSocket.getPort());

                // Submit client handler to thread pool
                executorService.submit(new ClientHandler(clientSocket, studentService, SKIP_JPA));
            }
        } catch (Exception e) {
            logger.error("✗ Server error: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}
