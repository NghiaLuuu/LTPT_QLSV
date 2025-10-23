package iuh.fit.se.server.net;

import iuh.fit.se.common.dto.*;
import iuh.fit.se.common.model.TaiKhoan;
import iuh.fit.se.common.model.UserRole;
import iuh.fit.se.common.protocol.Command;
import iuh.fit.se.common.protocol.Request;
import iuh.fit.se.common.protocol.Response;
import iuh.fit.se.common.protocol.Status;
import iuh.fit.se.server.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;

    // Services
    private final AuthService authService;
    private final KhoaService khoaService;
    private final LopHocService lopHocService;
    private final GiangVienService giangVienService;
    private final MonHocService monHocService;
    private final HocKyService hocKyService;
    private final HocVuService hocVuService;
    private final IStudentService studentService;

    public ClientHandler(Socket clientSocket, IStudentService studentService) {
        this.clientSocket = clientSocket;
        this.studentService = studentService;
        this.authService = AuthService.getInstance();
        this.khoaService = KhoaService.getInstance();
        this.lopHocService = LopHocService.getInstance();
        this.giangVienService = GiangVienService.getInstance();
        this.monHocService = MonHocService.getInstance();
        this.hocKyService = HocKyService.getInstance();
        this.hocVuService = HocVuService.getInstance();
    }

    @Override
    public void run() {
        String clientAddress = clientSocket.getInetAddress().getHostAddress();
        logger.info("Handling client: {}", clientAddress);

        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            Object obj;
            while ((obj = in.readObject()) != null) {
                if (obj instanceof Request) {
                    Request req = (Request) obj;
                    logger.debug("Received from {}: {}", clientAddress, req.getCommand());

                    Response res = handleRequest(req);

                    out.writeObject(res);
                    out.flush();
                    logger.debug("Sent to {}: {}", clientAddress, res.getStatus());
                }
            }

        } catch (Exception e) {
            logger.warn("Client {} disconnected or error: {}", clientAddress, e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (Exception ignore) {
            }
            logger.info("Connection closed for client: {}", clientAddress);
        }
    }

    private Response handleRequest(Request req) {
        try {
            Command cmd = req.getCommand();

            // BƯỚC 1: Xử lý LOGIN (không cần token)
            if (cmd == Command.LOGIN) {
                return handleLogin(req);
            }

            // BƯỚC 2: Kiểm tra authentication cho các lệnh khác
            String token = req.getAuthToken();
            if (!authService.validateToken(token)) {
                return new Response(Status.UNAUTHORIZED, "Chưa đăng nhập hoặc phiên đã hết hạn", null);
            }

            TaiKhoan account = authService.getAccountByToken(token);
            if (account == null) {
                return new Response(Status.UNAUTHORIZED, "Token không hợp lệ", null);
            }

            // BƯỚC 3: Xử lý LOGOUT
            if (cmd == Command.LOGOUT) {
                authService.logout(token);
                return new Response(Status.SUCCESS, "Đăng xuất thành công", null);
            }

            // BƯỚC 4: Xử lý các lệnh theo Command với Authorization
            return handleAuthenticatedRequest(req, account);

        } catch (Exception e) {
            logger.error("Error handling request: {}", e.getMessage(), e);
            return new Response(Status.ERROR, "Lỗi server: " + e.getMessage(), null);
        }
    }

    private Response handleLogin(Request req) {
        try {
            if (req.getData() instanceof LoginDTO) {
                LoginDTO loginDTO = (LoginDTO) req.getData();
                AuthResponseDTO authResponse = authService.login(loginDTO.getUsername(), loginDTO.getPassword());

                if (authResponse.isSuccess()) {
                    return new Response(Status.SUCCESS, authResponse.getMessage(), authResponse);
                } else {
                    return new Response(Status.ERROR, authResponse.getMessage(), null);
                }
            } else {
                return new Response(Status.VALIDATION_ERROR, "Dữ liệu không phải LoginDTO", null);
            }
        } catch (Exception e) {
            logger.error("Login error", e);
            return new Response(Status.ERROR, "Lỗi đăng nhập: " + e.getMessage(), null);
        }
    }

    private Response handleAuthenticatedRequest(Request req, TaiKhoan account) {
        Command cmd = req.getCommand();
        UserRole role = account.getRole();

        try {
            switch (cmd) {
                // ==================== QUẢN LÝ KHOA ====================
                case KHOA_ADD:
                    if (role != UserRole.ADMIN) return forbidden();
                    return new Response(Status.SUCCESS, "Thêm khoa thành công",
                        khoaService.addKhoa((KhoaDTO) req.getData()));

                case KHOA_UPDATE:
                    if (role != UserRole.ADMIN) return forbidden();
                    return new Response(Status.SUCCESS, "Cập nhật khoa thành công",
                        khoaService.updateKhoa((KhoaDTO) req.getData()));

                case KHOA_DELETE:
                    if (role != UserRole.ADMIN) return forbidden();
                    boolean deleted = khoaService.deleteKhoa((String) req.getData());
                    return deleted ? new Response(Status.SUCCESS, "Xóa khoa thành công", null)
                                   : new Response(Status.ERROR, "Xóa khoa thất bại", null);

                case KHOA_GET_BY_ID:
                    return new Response(Status.SUCCESS, "Lấy thông tin khoa",
                        khoaService.getKhoaById((String) req.getData()));

                case KHOA_GET_ALL:
                    return new Response(Status.SUCCESS, "Danh sách khoa",
                        khoaService.getAllKhoa());

                // ==================== QUẢN LÝ LỚP HỌC ====================
                case LOPHOC_ADD:
                    if (role != UserRole.ADMIN) return forbidden();
                    return new Response(Status.SUCCESS, "Thêm lớp học thành công",
                        lopHocService.addLopHoc((LopHocDTO) req.getData()));

                case LOPHOC_UPDATE:
                    if (role != UserRole.ADMIN) return forbidden();
                    return new Response(Status.SUCCESS, "Cập nhật lớp học thành công",
                        lopHocService.updateLopHoc((LopHocDTO) req.getData()));

                case LOPHOC_DELETE:
                    if (role != UserRole.ADMIN) return forbidden();
                    boolean lopDeleted = lopHocService.deleteLopHoc((String) req.getData());
                    return lopDeleted ? new Response(Status.SUCCESS, "Xóa lớp học thành công", null)
                                      : new Response(Status.ERROR, "Xóa lớp học thất bại", null);

                case LOPHOC_GET_ALL_BY_KHOA:
                    return new Response(Status.SUCCESS, "Danh sách lớp học",
                        lopHocService.getAllLopHocByKhoa((String) req.getData()));

                // ==================== QUẢN LÝ GIẢNG VIÊN ====================
                case GIANGVIEN_ADD:
                    if (role != UserRole.ADMIN) return forbidden();
                    return new Response(Status.SUCCESS, "Thêm giảng viên thành công",
                        giangVienService.addGiangVien((GiangVienDTO) req.getData()));

                case GIANGVIEN_UPDATE:
                    if (role != UserRole.ADMIN) return forbidden();
                    return new Response(Status.SUCCESS, "Cập nhật giảng viên thành công",
                        giangVienService.updateGiangVien((GiangVienDTO) req.getData()));

                case GIANGVIEN_DELETE:
                    if (role != UserRole.ADMIN) return forbidden();
                    boolean gvDeleted = giangVienService.deleteGiangVien((String) req.getData());
                    return gvDeleted ? new Response(Status.SUCCESS, "Xóa giảng viên thành công", null)
                                     : new Response(Status.ERROR, "Xóa giảng viên thất bại", null);

                case GIANGVIEN_GET_ALL_BY_KHOA:
                    return new Response(Status.SUCCESS, "Danh sách giảng viên",
                        giangVienService.getAllGiangVienByKhoa((String) req.getData()));

                // ==================== QUẢN LÝ MÔN HỌC ====================
                case MONHOC_ADD:
                    if (role != UserRole.ADMIN) return forbidden();
                    return new Response(Status.SUCCESS, "Thêm môn học thành công",
                        monHocService.addMonHoc((MonHocDTO) req.getData()));

                case MONHOC_UPDATE:
                    if (role != UserRole.ADMIN) return forbidden();
                    return new Response(Status.SUCCESS, "Cập nhật môn học thành công",
                        monHocService.updateMonHoc((MonHocDTO) req.getData()));

                case MONHOC_DELETE:
                    if (role != UserRole.ADMIN) return forbidden();
                    boolean mhDeleted = monHocService.deleteMonHoc((String) req.getData());
                    return mhDeleted ? new Response(Status.SUCCESS, "Xóa môn học thành công", null)
                                     : new Response(Status.ERROR, "Xóa môn học thất bại", null);

                case MONHOC_GET_ALL_BY_KHOA:
                    return new Response(Status.SUCCESS, "Danh sách môn học",
                        monHocService.getAllMonHocByKhoa((String) req.getData()));

                // ==================== QUẢN LÝ HỌC KỲ ====================
                case HOCKY_ADD:
                    if (role != UserRole.ADMIN) return forbidden();
                    return new Response(Status.SUCCESS, "Thêm học kỳ thành công",
                        hocKyService.addHocKy((HocKyDTO) req.getData()));

                case HOCKY_UPDATE:
                    if (role != UserRole.ADMIN) return forbidden();
                    return new Response(Status.SUCCESS, "Cập nhật học kỳ thành công",
                        hocKyService.updateHocKy((HocKyDTO) req.getData()));

                case HOCKY_DELETE:
                    if (role != UserRole.ADMIN) return forbidden();
                    boolean hkDeleted = hocKyService.deleteHocKy((String) req.getData());
                    return hkDeleted ? new Response(Status.SUCCESS, "Xóa học kỳ thành công", null)
                                     : new Response(Status.ERROR, "Xóa học kỳ thất bại", null);

                case HOCKY_GET_ALL:
                    return new Response(Status.SUCCESS, "Danh sách học kỳ",
                        hocKyService.getAllHocKy());

                // ==================== QUẢN LÝ LỚP HỌC PHẦN ====================
                case LOPHOCPHAN_ADD:
                    if (role != UserRole.ADMIN) return forbidden();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> lhpData = (Map<String, Object>) req.getData();
                    return new Response(Status.SUCCESS, "Mở lớp học phần thành công",
                        hocVuService.moLopHocPhan(
                            (String) lhpData.get("maMH"),
                            (String) lhpData.get("maGV"),
                            (String) lhpData.get("maHocKy"),
                            (Integer) lhpData.get("soLuongToiDa")
                        ));

                case LOPHOCPHAN_DELETE:
                    if (role != UserRole.ADMIN) return forbidden();
                    boolean lhpDeleted = hocVuService.deleteLopHocPhan((Long) req.getData());
                    return lhpDeleted ? new Response(Status.SUCCESS, "Xóa lớp học phần thành công", null)
                                      : new Response(Status.ERROR, "Xóa lớp học phần thất bại", null);

                case LOPHOCPHAN_GET_ALL_BY_HOCKY:
                    return new Response(Status.SUCCESS, "Danh sách lớp học phần",
                        hocVuService.getAllLopHocPhanByHocKy((String) req.getData()));

                // ==================== NGHIỆP VỤ SINH VIÊN ====================
                case SV_GET_LOPHOCPHAN_TO_REGISTER:
                    if (role != UserRole.SINH_VIEN) return forbidden();
                    return new Response(Status.SUCCESS, "Danh sách lớp có thể đăng ký",
                        hocVuService.getLopHocPhanToRegister((String) req.getData()));

                case SV_REGISTER_COURSE:
                    if (role != UserRole.SINH_VIEN) return forbidden();
                    String maSV = account.getSinhVien().getMaSV();
                    Long maLHP = (Long) req.getData();
                    boolean registered = hocVuService.dangKyHocPhan(maSV, maLHP);
                    return registered ? new Response(Status.SUCCESS, "Đăng ký học phần thành công", null)
                                      : new Response(Status.ERROR, "Đăng ký thất bại", null);

                // ==================== NGHIỆP VỤ GIẢNG VIÊN ====================
                case GV_GET_MY_CLASSES:
                    if (role != UserRole.GIANG_VIEN) return forbidden();
                    String maGV = account.getGiangVien().getMaGV();
                    return new Response(Status.SUCCESS, "Danh sách lớp giảng dạy",
                        hocVuService.getLopHocPhanByGiangVien(maGV));

                case GV_GET_STUDENT_LIST_OF_CLASS:
                    if (role != UserRole.GIANG_VIEN) return forbidden();
                    return new Response(Status.SUCCESS, "Danh sách sinh viên",
                        hocVuService.getDanhSachSinhVienByLopHocPhan((Long) req.getData()));

                case GV_ENTER_GRADE:
                    if (role != UserRole.GIANG_VIEN) return forbidden();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> gradeData = (Map<String, Object>) req.getData();
                    boolean gradeEntered = hocVuService.nhapDiem(
                        account.getGiangVien().getMaGV(),
                        (String) gradeData.get("maSV"),
                        (Long) gradeData.get("maLHP"),
                        (Double) gradeData.get("diemCC"),
                        (Double) gradeData.get("diemGK"),
                        (Double) gradeData.get("diemCK")
                    );
                    return gradeEntered ? new Response(Status.SUCCESS, "Nhập điểm thành công", null)
                                        : new Response(Status.ERROR, "Nhập điểm thất bại", null);

                // ==================== QUẢN LÝ SINH VIÊN (Legacy) ====================
                case ADD_STUDENT:
                case SINHVIEN_ADD:
                    if (role != UserRole.ADMIN) return forbidden();
                    boolean added = studentService.addStudent((iuh.fit.se.common.model.SinhVienDTO) req.getData());
                    return added ? new Response(Status.SUCCESS, "Thêm sinh viên thành công", null)
                                 : new Response(Status.ERROR, "Thêm thất bại", null);

                case FIND_STUDENT_BY_ID:
                case SINHVIEN_GET_BY_ID:
                    iuh.fit.se.common.model.SinhVienDTO sv = studentService.findStudentById((String) req.getData());
                    return sv != null ? new Response(Status.SUCCESS, "Tìm thấy sinh viên", sv)
                                      : new Response(Status.NOT_FOUND, "Không tìm thấy sinh viên", null);

                case GET_ALL_STUDENTS:
                case SINHVIEN_GET_ALL_BY_LOPHOC:
                    List<iuh.fit.se.common.model.SinhVienDTO> all = studentService.getAllStudents();
                    return new Response(Status.SUCCESS, "Danh sách sinh viên", all);

                case UPDATE_STUDENT:
                case SINHVIEN_UPDATE:
                    if (role != UserRole.ADMIN) return forbidden();
                    boolean updated = studentService.updateStudent((iuh.fit.se.common.model.SinhVienDTO) req.getData());
                    return updated ? new Response(Status.SUCCESS, "Cập nhật thành công", null)
                                   : new Response(Status.NOT_FOUND, "Cập nhật thất bại", null);

                case DELETE_STUDENT:
                case SINHVIEN_DELETE:
                    if (role != UserRole.ADMIN) return forbidden();
                    boolean svDeleted = studentService.deleteStudent((String) req.getData());
                    return svDeleted ? new Response(Status.SUCCESS, "Xóa thành công", null)
                                     : new Response(Status.NOT_FOUND, "Xóa thất bại", null);

                default:
                    return new Response(Status.ERROR, "Lệnh không hợp lệ: " + cmd, null);
            }
        } catch (ClassCastException e) {
            logger.error("Data type mismatch for command {}", cmd, e);
            return new Response(Status.VALIDATION_ERROR, "Dữ liệu không đúng định dạng", null);
        } catch (Exception e) {
            logger.error("Error handling command {}", cmd, e);
            return new Response(Status.ERROR, "Lỗi xử lý: " + e.getMessage(), null);
        }
    }

    private Response forbidden() {
        return new Response(Status.FORBIDDEN, "Không có quyền thực hiện thao tác này", null);
    }
}
