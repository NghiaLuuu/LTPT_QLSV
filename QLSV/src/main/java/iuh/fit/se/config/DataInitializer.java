package iuh.fit.se.config;

import iuh.fit.se.model.Role;
import iuh.fit.se.model.User;
import iuh.fit.se.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Component để khởi tạo dữ liệu ban đầu khi server start
 * Tự động tạo tài khoản admin nếu chưa có
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            // Kiểm tra xem đã có tài khoản admin chưa
            if (!userRepository.existsByUsername("admin")) {
                // Tạo tài khoản admin mới
                User admin = new User();
                admin.setUsername("admin"); // Bắt buộc phải có
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setRole(Role.ADMIN);
                admin.setActive(true);
                userRepository.save(admin);

                System.out.println("==================================================");
                System.out.println("✓ Tài khoản admin đã được tạo tự động!");
                System.out.println("  Username: admin");
                System.out.println("  Password: admin");
                System.out.println("  Vui lòng đổi mật khẩu sau khi đăng nhập lần đầu!");
                System.out.println("==================================================");
            } else {
                System.out.println("==================================================");
                System.out.println("✓ Tài khoản admin đã tồn tại");
                System.out.println("==================================================");
            }
        } catch (Exception e) {
            System.err.println("==================================================");
            System.err.println("✗ CẢNH BÁO: Không thể khởi tạo tài khoản admin tự động");
            System.err.println("  Lỗi: " + e.getMessage());
            System.err.println("  Vui lòng tạo tài khoản admin thủ công hoặc kiểm tra database");
            System.err.println("==================================================");
            e.printStackTrace();
        }
    }
}