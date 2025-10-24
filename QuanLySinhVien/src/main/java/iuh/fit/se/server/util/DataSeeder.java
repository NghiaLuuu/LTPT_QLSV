package iuh.fit.se.server.util;

import iuh.fit.se.common.dto.*;
import iuh.fit.se.common.model.Gender;
import iuh.fit.se.server.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

/**
 * Utility class để tạo dữ liệu mẫu khi khởi động server
 */
public class DataSeeder {
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    /**
     * Seed 3 khoa mẫu
     */
    public static void seedKhoa() {
        logger.info("🌱 Starting to seed Khoa data...");

        KhoaService khoaService = KhoaService.getInstance();

        String[][] khoaData = {
            {"CNTT", "Công nghệ Thông tin"},
            {"KHMT", "Khoa học Máy tính"},
            {"HTTT", "Hệ thống Thông tin"}
        };

        int created = 0;
        int skipped = 0;

        for (String[] data : khoaData) {
            try {
                String maKhoa = data[0];
                String tenKhoa = data[1];

                // Kiểm tra khoa đã tồn tại chưa
                try {
                    KhoaDTO existing = khoaService.getKhoaById(maKhoa);
                    if (existing != null) {
                        logger.debug("  ⊘ Khoa {} already exists, skipping", maKhoa);
                        skipped++;
                        continue;
                    }
                } catch (Exception e) {
                    // Khoa chưa tồn tại, tiếp tục tạo
                }

                KhoaDTO khoa = new KhoaDTO(maKhoa, tenKhoa);
                khoaService.addKhoa(khoa);
                logger.info("  ✓ Created khoa: {} - {}", maKhoa, tenKhoa);
                created++;

            } catch (Exception e) {
                logger.error("  ✗ Failed to create khoa {}: {}", data[0], e.getMessage());
            }
        }

        logger.info("🌱 Khoa seeding completed: {} created, {} skipped", created, skipped);
    }

    /**
     * Seed lớp hành chính
     */
    public static void seedLopHoc() {
        logger.info("🌱 Starting to seed LopHoc data...");

        LopHocService lopHocService = LopHocService.getInstance();

        String[][] lopHocData = {
            {"DHKTPM16A", "Đại học Kỹ thuật Phần mềm 16A", "2020-2024", "CNTT"},
            {"DHKTPM16B", "Đại học Kỹ thuật Phần mềm 16B", "2020-2024", "CNTT"},
            {"DHKTPM16C", "Đại học Kỹ thuật Phần mềm 16C", "2020-2024", "CNTT"},
            {"DHKHMT16A", "Đại học Khoa học Máy tính 16A", "2020-2024", "KHMT"},
            {"DHHTTT16A", "Đại học Hệ thống Thông tin 16A", "2020-2024", "HTTT"}
        };

        int created = 0;
        int skipped = 0;

        for (String[] data : lopHocData) {
            try {
                String maLop = data[0];
                String tenLop = data[1];
                String nienKhoa = data[2];
                String maKhoa = data[3];

                // Kiểm tra lớp đã tồn tại chưa
                try {
                    List<LopHocDTO> existing = lopHocService.getAllLopHocByKhoa(maKhoa);
                    if (existing.stream().anyMatch(l -> l.getMaLop().equals(maLop))) {
                        logger.debug("  ⊘ LopHoc {} already exists, skipping", maLop);
                        skipped++;
                        continue;
                    }
                } catch (Exception e) {
                    // Lớp chưa tồn tại, tiếp tục tạo
                }

                LopHocDTO lopHoc = new LopHocDTO(maLop, tenLop, nienKhoa, maKhoa, null);
                lopHocService.addLopHoc(lopHoc);
                logger.info("  ✓ Created lop hoc: {} - {}", maLop, tenLop);
                created++;

            } catch (Exception e) {
                logger.error("  ✗ Failed to create lop hoc {}: {}", data[0], e.getMessage());
            }
        }

        logger.info("🌱 LopHoc seeding completed: {} created, {} skipped", created, skipped);
    }

    /**
     * Seed môn học
     */
    public static void seedMonHoc() {
        logger.info("🌱 Starting to seed MonHoc data...");

        MonHocService monHocService = MonHocService.getInstance();

        String[][] monHocData = {
            {"LTHDT", "Lập trình Hướng đối tượng", "4", "CNTT"},
            {"CSDL", "Cơ sở Dữ liệu", "4", "CNTT"},
            {"CTDLGT", "Cấu trúc Dữ liệu và Giải thuật", "4", "CNTT"},
            {"LTPT", "Lập trình Phân tán", "4", "CNTT"},
            {"LTCB", "Lập trình C cơ bản", "3", "CNTT"},
            {"LTWEB", "Lập trình Web", "4", "CNTT"},
            {"TTNT", "Trí tuệ Nhân tạo", "4", "KHMT"},
            {"HTCSDL", "Hệ thống Cơ sở Dữ liệu", "4", "HTTT"},
            {"MMT", "Mạng Máy tính", "3", "CNTT"},
            {"PTTKHT", "Phân tích Thiết kế Hệ thống", "4", "HTTT"}
        };

        int created = 0;
        int skipped = 0;

        for (String[] data : monHocData) {
            try {
                String maMH = data[0];
                String tenMH = data[1];
                int soTinChi = Integer.parseInt(data[2]);
                String maKhoa = data[3];

                // Kiểm tra môn học đã tồn tại chưa
                try {
                    List<MonHocDTO> existing = monHocService.getAllMonHocByKhoa(maKhoa);
                    if (existing.stream().anyMatch(m -> m.getMaMH().equals(maMH))) {
                        logger.debug("  ⊘ MonHoc {} already exists, skipping", maMH);
                        skipped++;
                        continue;
                    }
                } catch (Exception e) {
                    // Môn học chưa tồn tại, tiếp tục tạo
                }

                MonHocDTO monHoc = new MonHocDTO(maMH, tenMH, soTinChi, maKhoa, null);
                monHocService.addMonHoc(monHoc);
                logger.info("  ✓ Created mon hoc: {} - {}", maMH, tenMH);
                created++;

            } catch (Exception e) {
                logger.error("  ✗ Failed to create mon hoc {}: {}", data[0], e.getMessage());
            }
        }

        logger.info("🌱 MonHoc seeding completed: {} created, {} skipped", created, skipped);
    }

    /**
     * Seed học kỳ
     */
    public static void seedHocKy() {
        logger.info("🌱 Starting to seed HocKy data...");

        HocKyService hocKyService = HocKyService.getInstance();

        Object[][] hocKyData = {
            {"HK1_2024", "Học kỳ 1 năm 2024-2025", "2024-09-01", "2025-01-15"},
            {"HK2_2024", "Học kỳ 2 năm 2024-2025", "2025-01-20", "2025-06-30"},
            {"HK1_2023", "Học kỳ 1 năm 2023-2024", "2023-09-01", "2024-01-15"},
            {"HK2_2023", "Học kỳ 2 năm 2023-2024", "2024-01-20", "2024-06-30"}
        };

        int created = 0;
        int skipped = 0;

        for (Object[] data : hocKyData) {
            try {
                String maHocKy = (String) data[0];
                String tenHocKy = (String) data[1];
                LocalDate ngayBatDau = LocalDate.parse((String) data[2]);
                LocalDate ngayKetThuc = LocalDate.parse((String) data[3]);

                // Kiểm tra học kỳ đã tồn tại chưa
                try {
                    List<HocKyDTO> existing = hocKyService.getAllHocKy();
                    if (existing.stream().anyMatch(h -> h.getMaHocKy().equals(maHocKy))) {
                        logger.debug("  ⊘ HocKy {} already exists, skipping", maHocKy);
                        skipped++;
                        continue;
                    }
                } catch (Exception e) {
                    // Học kỳ chưa tồn tại, tiếp tục tạo
                }

                HocKyDTO hocKy = new HocKyDTO(maHocKy, tenHocKy, ngayBatDau, ngayKetThuc);
                hocKyService.addHocKy(hocKy);
                logger.info("  ✓ Created hoc ky: {} - {}", maHocKy, tenHocKy);
                created++;

            } catch (Exception e) {
                logger.error("  ✗ Failed to create hoc ky {}: {}", data[0], e.getMessage());
            }
        }

        logger.info("🌱 HocKy seeding completed: {} created, {} skipped", created, skipped);
    }

    /**
     * Seed 20 sinh viên mẫu (mở rộng từ 10 lên 20)
     */
    public static void seedStudents(IStudentService studentService) {
        logger.info("🌱 Starting to seed student data...");

        String[][] studentData = {
            {"SV001", "Nguyễn Văn An", "2003-01-15", "NAM", "Kỹ thuật Phần mềm", "DHKTPM16A"},
            {"SV002", "Trần Thị Bích", "2003-02-20", "NU", "Kỹ thuật Phần mềm", "DHKTPM16A"},
            {"SV003", "Lê Văn Cường", "2003-03-10", "NAM", "Kỹ thuật Phần mềm", "DHKTPM16B"},
            {"SV004", "Phạm Thị Dung", "2003-04-25", "NU", "Hệ thống Thông tin", "DHHTTT16A"},
            {"SV005", "Hoàng Văn Em", "2003-05-30", "NAM", "Khoa học Máy tính", "DHKHMT16A"},
            {"SV006", "Võ Thị Phượng", "2003-06-15", "NU", "Kỹ thuật Phần mềm", "DHKTPM16B"},
            {"SV007", "Đặng Văn Giang", "2003-07-20", "NAM", "Hệ thống Thông tin", "DHHTTT16A"},
            {"SV008", "Bùi Thị Hoa", "2003-08-05", "NU", "Khoa học Máy tính", "DHKHMT16A"},
            {"SV009", "Phan Văn Inh", "2003-09-12", "NAM", "Kỹ thuật Phần mềm", "DHKTPM16A"},
            {"SV010", "Ngô Thị Kim", "2003-10-18", "NU", "Kỹ thuật Phần mềm", "DHKTPM16B"},
            {"SV011", "Trương Văn Long", "2003-11-22", "NAM", "Kỹ thuật Phần mềm", "DHKTPM16C"},
            {"SV012", "Mai Thị Minh", "2003-12-08", "NU", "Khoa học Máy tính", "DHKHMT16A"},
            {"SV013", "Đinh Văn Nam", "2003-01-28", "NAM", "Hệ thống Thông tin", "DHHTTT16A"},
            {"SV014", "Lý Thị Oanh", "2003-02-14", "NU", "Kỹ thuật Phần mềm", "DHKTPM16C"},
            {"SV015", "Dương Văn Phúc", "2003-03-19", "NAM", "Kỹ thuật Phần mềm", "DHKTPM16A"},
            {"SV016", "Chu Thị Quỳnh", "2003-04-30", "NU", "Kỹ thuật Phần mềm", "DHKTPM16B"},
            {"SV017", "Tô Văn Sơn", "2003-05-17", "NAM", "Khoa học Máy tính", "DHKHMT16A"},
            {"SV018", "Hồ Thị Tuyết", "2003-06-23", "NU", "Hệ thống Thông tin", "DHHTTT16A"},
            {"SV019", "Vũ Văn Uy", "2003-07-11", "NAM", "Kỹ thuật Phần mềm", "DHKTPM16C"},
            {"SV020", "Lưu Thị Vân", "2003-08-26", "NU", "Kỹ thuật Phần mềm", "DHKTPM16A"}
        };

        int created = 0;
        int skipped = 0;

        for (String[] data : studentData) {
            try {
                String maSV = data[0];

                // Kiểm tra xem sinh viên đã tồn tại chưa
                try {
                    SinhVienDTO existing = studentService.findStudentById(maSV);
                    if (existing != null) {
                        logger.debug("  ⊘ Student {} already exists, skipping", maSV);
                        skipped++;
                        continue;
                    }
                } catch (Exception e) {
                    // Sinh viên chưa tồn tại, tiếp tục tạo
                }

                // Tạo sinh viên mới
                String hoTen = data[1];
                LocalDate ngaySinh = LocalDate.parse(data[2]);
                Gender gioiTinh = data[3].equals("NAM") ? Gender.MALE : Gender.FEMALE;
                String chuyenNganh = data[4];
                String lop = data[5];
                double diemTB = 7.0 + (Math.random() * 2.5); // Random điểm từ 7.0 - 9.5

                SinhVienDTO sv = new SinhVienDTO(maSV, hoTen, ngaySinh, gioiTinh, chuyenNganh, lop, diemTB);
                studentService.addStudent(sv);

                logger.info("  ✓ Created student: {} - {}", maSV, hoTen);
                created++;

            } catch (Exception e) {
                logger.error("  ✗ Failed to create student {}: {}", data[0], e.getMessage());
            }
        }

        logger.info("🌱 Student seeding completed: {} created, {} skipped", created, skipped);
    }

    /**
     * Seed 15 giảng viên mẫu (mở rộng từ 10 lên 15)
     */
    public static void seedTeachers() {
        logger.info("🌱 Starting to seed teacher data...");

        GiangVienService giangVienService = GiangVienService.getInstance();
        AuthService authService = AuthService.getInstance();

        String[][] teacherData = {
            {"GV001", "TS. Nguyễn Văn Giáo", "Tiến sĩ", "CNTT"},
            {"GV002", "TS. Trần Thị Hương", "Tiến sĩ", "CNTT"},
            {"GV003", "ThS. Lê Văn Kiên", "Thạc sĩ", "CNTT"},
            {"GV004", "TS. Phạm Thị Lan", "Tiến sĩ", "KHMT"},
            {"GV005", "ThS. Hoàng Văn Minh", "Thạc sĩ", "CNTT"},
            {"GV006", "TS. Võ Thị Nga", "Tiến sĩ", "HTTT"},
            {"GV007", "ThS. Đặng Văn Oanh", "Thạc sĩ", "CNTT"},
            {"GV008", "TS. Bùi Thị Phượng", "Tiến sĩ", "KHMT"},
            {"GV009", "ThS. Phan Văn Quang", "Thạc sĩ", "HTTT"},
            {"GV010", "TS. Ngô Thị Rượu", "Tiến sĩ", "CNTT"},
            {"GV011", "ThS. Trương Văn Sáng", "Thạc sĩ", "CNTT"},
            {"GV012", "TS. Mai Thị Tâm", "Tiến sĩ", "KHMT"},
            {"GV013", "ThS. Đinh Văn Uy", "Thạc sĩ", "HTTT"},
            {"GV014", "TS. Lý Thị Vân", "Tiến sĩ", "CNTT"},
            {"GV015", "ThS. Dương Văn Xuân", "Thạc sĩ", "CNTT"}
        };

        int created = 0;
        int skipped = 0;

        for (String[] data : teacherData) {
            try {
                String maGV = data[0];
                String hoTen = data[1];
                String hocVi = data[2];
                String maKhoa = data[3];

                // Kiểm tra xem giảng viên đã tồn tại chưa
                try {
                    GiangVienDTO existing = giangVienService.getGiangVienById(maGV);
                    if (existing != null) {
                        logger.debug("  ⊘ Teacher {} already exists, skipping", maGV);
                        skipped++;
                        continue;
                    }
                } catch (Exception e) {
                    // Giảng viên chưa tồn tại, tiếp tục tạo
                }

                // Tạo giảng viên mới
                GiangVienDTO gv = new GiangVienDTO();
                gv.setMaGV(maGV);
                gv.setHoTen(hoTen);
                gv.setHocVi(hocVi);
                gv.setMaKhoa(maKhoa);

                giangVienService.addGiangVien(gv);
                logger.info("  ✓ Created teacher: {} - {}", maGV, hoTen);
                created++;

                // Tạo tài khoản cho giảng viên (username = maGV, password = 123456)
                String username = maGV.toLowerCase(); // gv001, gv002, ...
                try {
                    authService.createTeacherAccount(username, "123456", maGV);
                    logger.info("    → Created account: {} / 123456", username);
                } catch (Exception e) {
                    logger.debug("    ⊘ Account {} may already exist", username);
                }

            } catch (Exception e) {
                logger.error("  ✗ Failed to create teacher {}: {}", data[0], e.getMessage());
            }
        }

        logger.info("🌱 Teacher seeding completed: {} created, {} skipped", created, skipped);
    }

    /**
     * Seed lớp học phần
     */
    public static void seedLopHocPhan() {
        logger.info("🌱 Starting to seed LopHocPhan data...");

        HocVuService hocVuService = HocVuService.getInstance();

        Object[][] lopHocPhanData = {
            // Học kỳ 1/2024
            {"LTHDT", "GV001", "HK1_2024", 40},
            {"CSDL", "GV002", "HK1_2024", 40},
            {"CTDLGT", "GV003", "HK1_2024", 35},
            {"LTPT", "GV005", "HK1_2024", 30},
            {"LTWEB", "GV007", "HK1_2024", 35},
            {"TTNT", "GV004", "HK1_2024", 30},
            {"HTCSDL", "GV006", "HK1_2024", 35},
            {"MMT", "GV011", "HK1_2024", 40},

            // Học kỳ 2/2024
            {"LTHDT", "GV014", "HK2_2024", 40},
            {"CSDL", "GV010", "HK2_2024", 40},
            {"LTCB", "GV003", "HK2_2024", 45},
            {"LTWEB", "GV005", "HK2_2024", 35},
            {"PTTKHT", "GV009", "HK2_2024", 30},
            {"TTNT", "GV008", "HK2_2024", 30}
        };

        int created = 0;
        int skipped = 0;

        for (Object[] data : lopHocPhanData) {
            try {
                String maMH = (String) data[0];
                String maGV = (String) data[1];
                String maHocKy = (String) data[2];
                int soLuongToiDa = (int) data[3];

                // Kiểm tra xem lớp học phần đã tồn tại chưa
                try {
                    List<LopHocPhanDTO> existing = hocVuService.getAllLopHocPhanByHocKy(maHocKy);
                    boolean exists = existing.stream()
                        .anyMatch(l -> l.getMaMH().equals(maMH) && l.getMaGV().equals(maGV));
                    if (exists) {
                        logger.debug("  ⊘ LopHocPhan {}-{}-{} already exists, skipping", maMH, maGV, maHocKy);
                        skipped++;
                        continue;
                    }
                } catch (Exception e) {
                    // Lớp học phần chưa tồn tại, tiếp tục tạo
                }

                LopHocPhanDTO lhp = hocVuService.moLopHocPhan(maMH, maGV, maHocKy, soLuongToiDa);
                logger.info("  ✓ Created lop hoc phan: {} - {} - {} (ID: {})", maMH, maGV, maHocKy, lhp.getMaLHP());
                created++;

            } catch (Exception e) {
                logger.error("  ✗ Failed to create lop hoc phan {}-{}: {}", data[0], data[1], e.getMessage());
            }
        }

        logger.info("🌱 LopHocPhan seeding completed: {} created, {} skipped", created, skipped);
    }

    /**
     * Seed tài khoản cho sinh viên (username = maSV, password = 123456)
     */
    public static void seedStudentAccounts(IStudentService studentService) {
        logger.info("🌱 Starting to seed student accounts...");

        AuthService authService = AuthService.getInstance();
        int created = 0;
        int skipped = 0;

        try {
            // Lấy tất cả sinh viên
            List<SinhVienDTO> allStudents = studentService.getAllStudents();

            for (SinhVienDTO sv : allStudents) {
                String username = sv.getMaSV().toLowerCase(); // sv001, sv002, ...
                String maSV = sv.getMaSV();

                try {
                    authService.createStudentAccount(username, "123456", maSV);
                    logger.info("  ✓ Created account for {}: {} / 123456", maSV, username);
                    created++;
                } catch (Exception e) {
                    logger.debug("  ⊘ Account {} may already exist, skipping", username);
                    skipped++;
                }
            }

        } catch (Exception e) {
            logger.error("✗ Failed to seed student accounts: {}", e.getMessage());
        }

        logger.info("🌱 Student accounts seeding completed: {} created, {} skipped", created, skipped);
    }

    /**
     * Seed tất cả dữ liệu mẫu theo đúng thứ tự phụ thuộc
     */
    public static void seedAll(IStudentService studentService) {
        logger.info("========================================");
        logger.info("🌱 STARTING DATA SEEDING");
        logger.info("========================================");

        try {
            // 1. Seed Khoa (phải tạo trước vì các entity khác phụ thuộc)
            seedKhoa();

            // 2. Seed Lớp hành chính
            seedLopHoc();

            // 3. Seed Môn học
            seedMonHoc();

            // 4. Seed Học kỳ
            seedHocKy();

            // 5. Seed Giảng viên (phụ thuộc vào Khoa)
            seedTeachers();

            // 6. Seed Sinh viên (phụ thuộc vào Lớp)
            seedStudents(studentService);

            // 7. Seed Lớp học phần (phụ thuộc vào Môn học, Giảng viên, Học kỳ)
            seedLopHocPhan();

            // 8. Seed tài khoản cho sinh viên
            seedStudentAccounts(studentService);

            logger.info("========================================");
            logger.info("🌱 DATA SEEDING COMPLETED SUCCESSFULLY");
            logger.info("========================================");
            logger.info("📊 Summary:");
            logger.info("   - 3 Khoa (CNTT, KHMT, HTTT)");
            logger.info("   - 5 Lớp hành chính");
            logger.info("   - 10 Môn học");
            logger.info("   - 4 Học kỳ");
            logger.info("   - 15 Giảng viên");
            logger.info("   - 20 Sinh viên");
            logger.info("   - 14 Lớp học phần");
            logger.info("========================================");

        } catch (Exception e) {
            logger.error("========================================");
            logger.error("✗ DATA SEEDING FAILED: {}", e.getMessage(), e);
            logger.error("========================================");
        }
    }
}
