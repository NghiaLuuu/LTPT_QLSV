package iuh.fit.se.client;

import iuh.fit.se.client.net.NetworkClient;
import iuh.fit.se.common.dto.SinhVienDTO;
import iuh.fit.se.common.model.Gender;
import iuh.fit.se.common.protocol.Command;
import iuh.fit.se.common.protocol.Request;
import iuh.fit.se.common.protocol.Response;
import iuh.fit.se.common.protocol.Status;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888; // ĐỔI PORT từ 8080 → 8888
    private static NetworkClient networkClient;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            networkClient = new NetworkClient(SERVER_HOST, SERVER_PORT);
            System.out.println("=== KẾT NỐI THÀNH CÔNG ĐẾN SERVER ===\n");

            boolean running = true;
            while (running) {
                showMenu();
                int choice = readInt("Chọn chức năng: ", 0, 6);
                System.out.println();

                switch (choice) {
                    case 1:
                        themSinhVien();
                        break;
                    case 2:
                        capNhatSinhVien();
                        break;
                    case 3:
                        xoaSinhVien();
                        break;
                    case 4:
                        timSinhVien();
                        break;
                    case 5:
                        xemTatCaSinhVien();
                        break;
                    case 0:
                        running = false;
                        System.out.println("Đang ngắt kết nối...");
                        break;
                    default:
                        System.out.println("Lựa chọn không hợp lệ!");
                }
                System.out.println();
            }

        } catch (Exception e) {
            System.err.println("Lỗi kết nối đến server: " + e.getMessage());
        } finally {
            if (networkClient != null) {
                networkClient.close();
            }
            scanner.close();
        }
    }

    private static void showMenu() {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║   HỆ THỐNG QUẢN LÝ SINH VIÊN         ║");
        System.out.println("╠═══════════════════════════════���═══════╣");
        System.out.println("║ 1. Thêm sinh viên                     ║");
        System.out.println("║ 2. Cập nhật sinh viên                 ║");
        System.out.println("║ 3. Xóa sinh viên                      ║");
        System.out.println("║ 4. Tìm sinh viên theo mã             ║");
        System.out.println("║ 5. Xem tất cả sinh viên               ║");
        System.out.println("║ 0. Thoát                              ║");
        System.out.println("╚═══════════════════════════════════════╝");
    }

    // === CRUD Functions ===

    private static void themSinhVien() {
        System.out.println("--- THÊM SINH VIÊN MỚI ---");
        String maSV = readString("Nhập mã SV (5-20 ký tự): ");
        String hoTen = readString("Nhập họ tên: ");
        LocalDate ngaySinh = readLocalDate("Nhập ngày sinh (YYYY-MM-DD): ");
        Gender gioiTinh = readGender("Nhập giới tính");
        String chuyenNganh = readString("Nhập chuyên ngành: ");
        String lop = readString("Nhập lớp: ");
        double diemTB = readDouble("Nhập điểm TB (0-10): ", 0.0, 10.0);

        SinhVienDTO sv = new SinhVienDTO(maSV, hoTen, ngaySinh, gioiTinh, chuyenNganh, lop, diemTB);

        Request req = new Request(Command.ADD_STUDENT, sv);
        Response res = networkClient.sendRequest(req);

        if (res.getStatus() == Status.SUCCESS) {
            System.out.println("✓ " + res.getMessage());
        } else {
            System.out.println("✗ " + res.getMessage());
        }
    }

    private static void capNhatSinhVien() {
        System.out.println("--- CẬP NHẬT SINH VIÊN ---");
        String maSV = readString("Nhập mã SV cần cập nhật: ");

        // Tìm sinh viên trước
        Request findReq = new Request(Command.FIND_STUDENT_BY_ID, maSV);
        Response findRes = networkClient.sendRequest(findReq);

        if (findRes.getStatus() != Status.SUCCESS) {
            System.out.println("✗ " + findRes.getMessage());
            return;
        }

        SinhVienDTO sv = (SinhVienDTO) findRes.getData();
        System.out.println("\nThông tin hiện tại:");
        printSinhVien(sv);
        System.out.println("\n(Nhấn Enter để giữ nguyên giá trị cũ)");

        // Cho phép cập nhật từng trường
        System.out.print("Nhập họ tên mới: ");
        String hoTen = scanner.nextLine().trim();
        if (!hoTen.isEmpty()) sv.setHoTen(hoTen);

        System.out.print("Nhập ngày sinh mới (YYYY-MM-DD): ");
        String ngaySinhStr = scanner.nextLine().trim();
        if (!ngaySinhStr.isEmpty()) {
            try {
                sv.setNgaySinh(LocalDate.parse(ngaySinhStr));
            } catch (DateTimeParseException e) {
                System.out.println("Định dạng ngày không hợp lệ, giữ nguyên giá trị cũ.");
            }
        }

        System.out.print("Nhập giới tính mới (MALE/FEMALE/OTHER): ");
        String genderStr = scanner.nextLine().trim().toUpperCase();
        if (!genderStr.isEmpty()) {
            try {
                sv.setGioiTinh(Gender.valueOf(genderStr));
            } catch (IllegalArgumentException e) {
                System.out.println("Giới tính không hợp lệ, giữ nguyên giá trị cũ.");
            }
        }

        System.out.print("Nhập chuyên ngành mới: ");
        String chuyenNganh = scanner.nextLine().trim();
        if (!chuyenNganh.isEmpty()) sv.setChuyenNganh(chuyenNganh);

        System.out.print("Nhập lớp mới: ");
        String lop = scanner.nextLine().trim();
        if (!lop.isEmpty()) sv.setLop(lop);

        System.out.print("Nhập điểm TB mới (0-10): ");
        String diemStr = scanner.nextLine().trim();
        if (!diemStr.isEmpty()) {
            try {
                double diem = Double.parseDouble(diemStr);
                if (diem >= 0 && diem <= 10) {
                    sv.setDiemTB(diem);
                } else {
                    System.out.println("Điểm không hợp lệ, giữ nguyên giá trị cũ.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Điểm không hợp lệ, giữ nguyên giá trị cũ.");
            }
        }

        // Gửi request cập nhật
        Request updateReq = new Request(Command.UPDATE_STUDENT, sv);
        Response updateRes = networkClient.sendRequest(updateReq);

        if (updateRes.getStatus() == Status.SUCCESS) {
            System.out.println("✓ " + updateRes.getMessage());
        } else {
            System.out.println("✗ " + updateRes.getMessage());
        }
    }

    private static void xoaSinhVien() {
        System.out.println("--- XÓA SINH VIÊN ---");
        String maSV = readString("Nhập mã SV cần xóa: ");

        System.out.print("Bạn có chắc chắn muốn xóa sinh viên " + maSV + "? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (!confirm.equals("Y")) {
            System.out.println("Đã hủy thao tác xóa.");
            return;
        }

        Request req = new Request(Command.DELETE_STUDENT, maSV);
        Response res = networkClient.sendRequest(req);

        if (res.getStatus() == Status.SUCCESS) {
            System.out.println("✓ " + res.getMessage());
        } else {
            System.out.println("✗ " + res.getMessage());
        }
    }

    private static void timSinhVien() {
        System.out.println("--- TÌM SINH VIÊN ---");
        String maSV = readString("Nhập mã SV cần tìm: ");

        Request req = new Request(Command.FIND_STUDENT_BY_ID, maSV);
        Response res = networkClient.sendRequest(req);

        if (res.getStatus() == Status.SUCCESS) {
            SinhVienDTO sv = (SinhVienDTO) res.getData();
            System.out.println("\n✓ Tìm thấy sinh viên:");
            printSinhVien(sv);
        } else {
            System.out.println("✗ " + res.getMessage());
        }
    }

    private static void xemTatCaSinhVien() {
        System.out.println("--- DANH SÁCH SINH VIÊN ---");

        Request req = new Request(Command.GET_ALL_STUDENTS, null);
        Response res = networkClient.sendRequest(req);

        if (res.getStatus() == Status.SUCCESS) {
            @SuppressWarnings("unchecked")
            List<SinhVienDTO> list = (List<SinhVienDTO>) res.getData();
            if (list.isEmpty()) {
                System.out.println("Chưa có sinh viên nào trong hệ thống.");
            } else {
                System.out.println("Tổng số: " + list.size() + " sinh viên\n");
                System.out.println(repeatChar('─', 120));
                System.out.printf("%-12s %-25s %-12s %-10s %-20s %-10s %-8s%n",
                        "Mã SV", "Họ tên", "Ngày sinh", "Giới tính", "Chuyên ngành", "Lớp", "Điểm TB");
                System.out.println(repeatChar('─', 120));
                for (SinhVienDTO sv : list) {
                    System.out.printf("%-12s %-25s %-12s %-10s %-20s %-10s %-8.2f%n",
                            sv.getMaSV(), sv.getHoTen(), sv.getNgaySinh(),
                            sv.getGioiTinh(), sv.getChuyenNganh(), sv.getLop(), sv.getDiemTB());
                }
                System.out.println(repeatChar('─', 120));
            }
        } else {
            System.out.println("✗ " + res.getMessage());
        }
    }

    // === Helper Functions ===

    private static void printSinhVien(SinhVienDTO sv) {
        System.out.println("┌" + repeatChar('─', 50) + "┐");
        System.out.printf("│ %-20s: %-25s │%n", "Mã SV", sv.getMaSV());
        System.out.printf("│ %-20s: %-25s │%n", "Họ tên", sv.getHoTen());
        System.out.printf("│ %-20s: %-25s │%n", "Ngày sinh", sv.getNgaySinh());
        System.out.printf("│ %-20s: %-25s │%n", "Giới tính", sv.getGioiTinh());
        System.out.printf("│ %-20s: %-25s │%n", "Chuyên ngành", sv.getChuyenNganh());
        System.out.printf("│ %-20s: %-25s │%n", "Lớp", sv.getLop());
        System.out.printf("│ %-20s: %-25.2f │%n", "Điểm TB", sv.getDiemTB());
        System.out.println("└" + repeatChar('─', 50) + "┘");
    }

    // Helper method to repeat characters (Java 8 compatible)
    private static String repeatChar(char ch, int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        while (input.isEmpty()) {
            System.out.println("Không được để trống. Vui lòng nhập lại.");
            System.out.print(prompt);
            input = scanner.nextLine().trim();
        }
        return input;
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int val = Integer.parseInt(scanner.nextLine().trim());
                if (val < min || val > max) {
                    System.out.printf("Giá trị phải nằm trong khoảng [%d, %d].%n", min, max);
                } else {
                    return val;
                }
            } catch (NumberFormatException e) {
                System.out.println("Nhập sai định dạng số. Vui lòng nhập lại.");
            }
        }
    }

    private static double readDouble(String prompt, double min, double max) {
        while (true) {
            try {
                System.out.print(prompt);
                double val = Double.parseDouble(scanner.nextLine().trim());
                if (val < min || val > max) {
                    System.out.printf("Giá trị phải nằm trong khoảng [%.1f, %.1f].%n", min, max);
                } else {
                    return val;
                }
            } catch (NumberFormatException e) {
                System.out.println("Nhập sai định dạng số. Vui lòng nhập lại.");
            }
        }
    }

    private static LocalDate readLocalDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Nhập sai định dạng ngày (YYYY-MM-DD). Vui lòng nhập lại.");
            }
        }
    }

    private static Gender readGender(String prompt) {
        while (true) {
            System.out.print(prompt + " (MALE, FEMALE, OTHER): ");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return Gender.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Giá trị không hợp lệ. Vui lòng chọn MALE, FEMALE, hoặc OTHER.");
            }
        }
    }
}
