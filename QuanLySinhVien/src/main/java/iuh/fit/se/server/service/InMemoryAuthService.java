package iuh.fit.se.server.service;

import iuh.fit.se.common.dto.AuthResponseDTO;
import iuh.fit.se.common.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory AuthService - không cần database
 */
public class InMemoryAuthService {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryAuthService.class);
    private static InMemoryAuthService instance;

    // Lưu trữ tài khoản: username -> (password, role, maSV, maGV)
    private final Map<String, UserAccount> accounts = new HashMap<>();

    // Lưu trữ token và username
    private final ConcurrentHashMap<String, String> activeTokens = new ConcurrentHashMap<>();

    // Lưu trữ thông tin session
    private final ConcurrentHashMap<String, UserAccount> activeSessions = new ConcurrentHashMap<>();

    private InMemoryAuthService() {
        initializeDefaultAccounts();
    }

    public static synchronized InMemoryAuthService getInstance() {
        if (instance == null) {
            instance = new InMemoryAuthService();
        }
        return instance;
    }

    private void initializeDefaultAccounts() {
        // Tài khoản Admin
        accounts.put("admin", new UserAccount("admin", "admin123", UserRole.ADMIN, null, null));

        // Tài khoản Sinh viên mẫu
        accounts.put("sv001", new UserAccount("sv001", "123456", UserRole.SINH_VIEN, "SV001", null));
        accounts.put("sv002", new UserAccount("sv002", "123456", UserRole.SINH_VIEN, "SV002", null));

        // Tài khoản Giảng viên mẫu
        accounts.put("gv001", new UserAccount("gv001", "123456", UserRole.GIANG_VIEN, null, "GV001"));
        accounts.put("gv002", new UserAccount("gv002", "123456", UserRole.GIANG_VIEN, null, "GV002"));

        logger.info("✓ Initialized {} default accounts", accounts.size());
        logger.info("  → Admin: admin/admin123");
        logger.info("  → Sinh viên: sv001/123456, sv002/123456");
        logger.info("  → Giảng viên: gv001/123456, gv002/123456");
    }

    /**
     * Đăng nhập
     */
    public AuthResponseDTO login(String username, String password) {
        UserAccount account = accounts.get(username);

        if (account == null) {
            logger.warn("Login attempt with non-existent username '{}'", username);
            return new AuthResponseDTO(false, "Tài khoản không tồn tại", null, null, null, null, null);
        }

        if (!account.password.equals(password)) {
            logger.warn("Failed login attempt for user '{}'", username);
            return new AuthResponseDTO(false, "Sai mật khẩu", null, null, null, null, null);
        }

        // Tạo token
        String token = UUID.randomUUID().toString();
        activeTokens.put(token, username);
        activeSessions.put(username, account);

        logger.info("User '{}' logged in successfully with role {}", username, account.role);

        return new AuthResponseDTO(
            true,
            "Đăng nhập thành công",
            token,
            username,
            account.role,
            account.maSV,
            account.maGV
        );
    }

    /**
     * Đăng xuất
     */
    public boolean logout(String token) {
        String username = activeTokens.remove(token);
        if (username != null) {
            activeSessions.remove(username);
            logger.info("User '{}' logged out", username);
            return true;
        }
        return false;
    }

    /**
     * Kiểm tra token hợp lệ
     */
    public boolean validateToken(String token) {
        return token != null && activeTokens.containsKey(token);
    }

    /**
     * Lấy thông tin account từ token
     */
    public UserAccount getAccountByToken(String token) {
        if (token == null) return null;
        String username = activeTokens.get(token);
        return username != null ? activeSessions.get(username) : null;
    }

    /**
     * Inner class để lưu thông tin tài khoản
     */
    public static class UserAccount {
        public final String username;
        public final String password;
        public final UserRole role;
        public final String maSV;
        public final String maGV;

        public UserAccount(String username, String password, UserRole role, String maSV, String maGV) {
            this.username = username;
            this.password = password;
            this.role = role;
            this.maSV = maSV;
            this.maGV = maGV;
        }

        public UserRole getRole() {
            return role;
        }

        public String getMaSV() {
            return maSV;
        }

        public String getMaGV() {
            return maGV;
        }
    }
}

