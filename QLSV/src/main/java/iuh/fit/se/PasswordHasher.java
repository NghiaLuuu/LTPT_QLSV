package iuh.fit.se;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Scanner;

public class PasswordHasher {

    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Nhập mật khẩu muốn mã hóa: ");
        String rawPassword = scanner.nextLine();

        String hashedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("Mật khẩu đã mã hóa: " + hashedPassword);
    }
}
