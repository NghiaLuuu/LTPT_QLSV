package iuh.fit.se.server.service;

import iuh.fit.se.common.dto.AuthResponseDTO;
import iuh.fit.se.common.model.TaiKhoan;
import iuh.fit.se.common.model.UserRole;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service xử lý Authentication (Xác thực) và Authorization (Phân quyền)
 */
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static AuthService instance;
    private final EntityManagerFactory emf;

    // Lưu trữ token và username (token -> username)
    private final ConcurrentHashMap<String, String> activeTokens = new ConcurrentHashMap<>();

    // Lưu trữ thông tin tài khoản đã login (username -> TaiKhoan)
    private final ConcurrentHashMap<String, TaiKhoan> activeSessions = new ConcurrentHashMap<>();

    private AuthService() {
        this.emf = Persistence.createEntityManagerFactory("StudentPU_SQLServer");
    }

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    /**
     * Đăng nhập - kiểm tra username/password và tạo token
     */
    public AuthResponseDTO login(String username, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            // Tìm tài khoản theo username
            TaiKhoan taiKhoan = em.createQuery(
                "SELECT t FROM TaiKhoan t WHERE t.username = :username", TaiKhoan.class)
                .setParameter("username", username)
                .getSingleResult();

            // Kiểm tra mật khẩu (so sánh hash)
            if (BCrypt.checkpw(password, taiKhoan.getPassword())) {
                // Tạo token mới (UUID)
                String token = UUID.randomUUID().toString();

                // Lưu token và session
                activeTokens.put(token, username);
                activeSessions.put(username, taiKhoan);

                logger.info("User '{}' logged in successfully with role {}", username, taiKhoan.getRole());

                // Trả về thông tin đăng nhập
                return new AuthResponseDTO(
                    true,
                    "Đăng nhập thành công",
                    token,
                    username,
                    taiKhoan.getRole(),
                    taiKhoan.getSinhVien() != null ? taiKhoan.getSinhVien().getMaSV() : null,
                    taiKhoan.getGiangVien() != null ? taiKhoan.getGiangVien().getMaGV() : null
                );
            } else {
                logger.warn("Failed login attempt for user '{}'", username);
                return new AuthResponseDTO(false, "Sai mật khẩu", null, null, null, null, null);
            }

        } catch (NoResultException e) {
            logger.warn("Login attempt with non-existent username '{}'", username);
            return new AuthResponseDTO(false, "Tài khoản không tồn tại", null, null, null, null, null);
        } catch (Exception e) {
            logger.error("Error during login", e);
            return new AuthResponseDTO(false, "Lỗi hệ thống: " + e.getMessage(), null, null, null, null, null);
        } finally {
            em.close();
        }
    }

    /**
     * Đăng xuất - xóa token
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
     * Kiểm tra token có hợp lệ không
     */
    public boolean validateToken(String token) {
        return token != null && activeTokens.containsKey(token);
    }

    /**
     * Lấy thông tin tài khoản từ token
     */
    public TaiKhoan getAccountByToken(String token) {
        if (token == null) return null;
        String username = activeTokens.get(token);
        return username != null ? activeSessions.get(username) : null;
    }

    /**
     * Tạo tài khoản mới với mật khẩu đã hash
     */
    public boolean createAccount(TaiKhoan taiKhoan, String plainPassword) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Hash mật khẩu trước khi lưu
            String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
            taiKhoan.setPassword(hashedPassword);

            em.persist(taiKhoan);
            em.getTransaction().commit();

            logger.info("Created new account: {}", taiKhoan.getUsername());
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error creating account", e);
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Đổi mật khẩu
     */
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        EntityManager em = emf.createEntityManager();
        try {
            TaiKhoan taiKhoan = em.find(TaiKhoan.class, username);
            if (taiKhoan == null) return false;

            // Kiểm tra mật khẩu cũ
            if (!BCrypt.checkpw(oldPassword, taiKhoan.getPassword())) {
                return false;
            }

            em.getTransaction().begin();

            // Hash và cập nhật mật khẩu mới
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
            taiKhoan.setPassword(hashedPassword);

            em.getTransaction().commit();

            logger.info("Password changed for user '{}'", username);
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error changing password", e);
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Khởi tạo tài khoản admin mặc định nếu chưa có
     * Username: admin
     * Password: admin123
     */
    public void initializeDefaultAdmin() {
        EntityManager em = emf.createEntityManager();
        try {
            // Kiểm tra xem đã có tài khoản admin chưa
            TaiKhoan existingAdmin = em.createQuery(
                "SELECT t FROM TaiKhoan t WHERE t.username = :username", TaiKhoan.class)
                .setParameter("username", "admin")
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

            if (existingAdmin == null) {
                // Chưa có admin, tạo mới
                logger.info("No admin account found. Creating default admin account...");

                em.getTransaction().begin();

                TaiKhoan adminAccount = new TaiKhoan();
                adminAccount.setUsername("admin");
                adminAccount.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt(12)));
                adminAccount.setRole(UserRole.ADMIN);

                em.persist(adminAccount);
                em.getTransaction().commit();

                logger.info("========================================");
                logger.info("✓ Default admin account created!");
                logger.info("  Username: admin");
                logger.info("  Password: admin123");
                logger.info("========================================");
            } else {
                logger.info("Admin account already exists.");
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error initializing default admin account", e);
        } finally {
            em.close();
        }
    }

    public void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    /**
     * Tạo tài khoản cho sinh viên
     */
    public TaiKhoan createStudentAccount(String username, String password, String maSV) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Kiểm tra username đã tồn tại chưa
            try {
                TaiKhoan existing = em.createQuery("SELECT t FROM TaiKhoan t WHERE t.username = :username", TaiKhoan.class)
                    .setParameter("username", username)
                    .getSingleResult();
                throw new RuntimeException("Username '" + username + "' đã tồn tại");
            } catch (NoResultException e) {
                // Username chưa tồn tại, OK để tạo mới
            }

            // Tìm sinh viên
            iuh.fit.se.common.model.SinhVien sinhVien = em.createQuery("SELECT s FROM SinhVien s WHERE s.maSV = :maSV", iuh.fit.se.common.model.SinhVien.class)
                .setParameter("maSV", maSV)
                .getSingleResult();

            // Tạo tài khoản mới
            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setUsername(username);
            taiKhoan.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(12)));
            taiKhoan.setRole(UserRole.SINH_VIEN);
            taiKhoan.setSinhVien(sinhVien);

            em.persist(taiKhoan);
            em.getTransaction().commit();

            logger.info("Created student account: {} for {}", username, maSV);
            return taiKhoan;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi tạo tài khoản sinh viên: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Tạo tài khoản cho giảng viên
     */
    public TaiKhoan createTeacherAccount(String username, String password, String maGV) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Kiểm tra username đã tồn tại chưa
            try {
                TaiKhoan existing = em.createQuery("SELECT t FROM TaiKhoan t WHERE t.username = :username", TaiKhoan.class)
                    .setParameter("username", username)
                    .getSingleResult();
                throw new RuntimeException("Username '" + username + "' đã tồn tại");
            } catch (NoResultException e) {
                // Username chưa tồn tại, OK để tạo mới
            }

            // Tìm giảng viên
            iuh.fit.se.common.model.GiangVien giangVien = em.createQuery("SELECT g FROM GiangVien g WHERE g.maGV = :maGV", iuh.fit.se.common.model.GiangVien.class)
                .setParameter("maGV", maGV)
                .getSingleResult();

            // Tạo tài khoản mới
            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setUsername(username);
            taiKhoan.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(12)));
            taiKhoan.setRole(UserRole.GIANG_VIEN);
            taiKhoan.setGiangVien(giangVien);

            em.persist(taiKhoan);
            em.getTransaction().commit();

            logger.info("Created teacher account: {} for {}", username, maGV);
            return taiKhoan;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi tạo tài khoản giảng viên: " + e.getMessage());
        } finally {
            em.close();
        }
    }
}
