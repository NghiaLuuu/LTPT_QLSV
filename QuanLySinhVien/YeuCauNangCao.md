Yêu cầu hiện tại đúng là chỉ ở mức quản lý danh sách đơn giản.

Để nâng cấp thành một hệ thống "quản lý sinh viên của một trường đại học", chúng ta cần một mô hình dữ liệu (data model) và logic nghiệp vụ (business logic) phức tạp hơn rất nhiều. Hệ thống sẽ không chỉ quản lý `SinhVien`, mà còn phải quản lý **Khoa, Lớp, Môn học, Giảng viên, Điểm số, Đăng ký học phần** và **Tài khoản (Phân quyền)**.

Đây là **bản mô tả dự án được viết lại toàn bộ** để đáp ứng yêu cầu chi tiết của một trường đại học.

-----

## Mô tả Dự án Chi tiết (Phiên bản Nâng cao): Hệ thống Thông tin Quản lý Đào tạo

**(JPA + Socket + SQL Server + Phân quyền)**

### 1\. Tổng quan & Triết lý Thiết kế

Xây dựng một hệ thống thông tin quản lý đào tạo (Academic Information System) theo mô hình Client-Server, mô phỏng các nghiệp vụ cốt lõi của một trường đại học.

* **Mục tiêu:** Không chỉ là CRUD (Thêm/Sửa/Xóa/Xem), hệ thống phải xử lý được các **logic nghiệp vụ phức tạp** như:

    * Phân quyền (Authentication & Authorization) dựa trên vai trò (Role).
    * Quản lý các đơn vị (Khoa, Lớp).
    * Quản lý chương trình học (Môn học).
    * Quản lý giảng dạy (Giảng viên, Mở lớp học phần).
    * Xử lý học vụ (Sinh viên đăng ký học phần, Giảng viên nhập điểm, Tính điểm trung bình).

* **Server:** Vẫn là ứng dụng Java đa luồng (Thread Pool), sử dụng JPA (Hibernate) với SQL Server. Server là nơi thực thi **toàn bộ logic nghiệp vụ và kiểm tra phân quyền**.

* **Client(s):** Hệ thống này sẽ cần nhiều hơn một Client:

    1.  **`admin-client` (Console hoặc Swing):** Dành cho Quản trị viên/Giáo vụ. Có toàn quyền quản lý các danh mục (Khoa, Lớp, Môn học, Giảng viên, Sinh viên, Tài khoản).
    2.  **(Mở rộng) `lecturer-client`:** Giao diện cho Giảng viên (nhập điểm cho lớp mình dạy).
    3.  **(Mở rộng) `student-client`:** Giao diện cho Sinh viên (xem điểm, đăng ký học phần).

* **Common Library:** Vẫn là module `.jar` chung, nhưng sẽ chứa một hệ thống Entity và Giao thức phức tạp hơn nhiều.

-----

### 2\. Module `common-lib` (Hợp đồng Dữ liệu)

Đây là phần mở rộng quan trọng nhất. Chúng ta sẽ mô hình hóa CSDL quan hệ của một trường đại học.

#### 2.1. Các Entity Mới (Entities)

*(Tất cả đều `implements Serializable` và có các annotation JPA)*

**A. Nhóm Đơn vị & Con người**

1.  **`TaiKhoan.java` (Account) - Entity BẮT BUỘC**

    * `username` (String, PK): Tên đăng nhập.
    * `password` (String): **Mật khẩu đã được hash (băm)**, không bao giờ lưu text gốc.
    * `role` (Enum `UserRole`): Enum định nghĩa các vai trò (`ADMIN`, `GIANG_VIEN`, `SINH_VIEN`).
    * `@OneToOne` `sinhVien`: Liên kết 1-1 tới `SinhVien` (nếu role là `SINH_VIEN`).
    * `@OneToOne` `giangVien`: Liên kết 1-1 tới `GiangVien` (nếu role là `GIANG_VIEN`).

2.  **`Khoa.java` (Faculty/Department)**

    * `maKhoa` (String, PK): "CNTT", "KT".
    * `tenKhoa` (String): "Công nghệ thông tin", "Kinh tế".
    * `@OneToMany` `dsLopHoc`: Danh sách các lớp hành chính thuộc khoa.
    * `@OneToMany` `dsGiangVien`: Danh sách giảng viên thuộc khoa.
    * `@OneToMany` `dsMonHoc`: Danh sách môn học do khoa quản lý.

3.  **`LopHoc.java` (Administrative Class)** - Lớp sinh hoạt.

    * `maLop` (String, PK): "D20CNPM1".
    * `tenLop` (String): "Đại học CNTT 2020 - Lớp 1".
    * `nienKhoa` (String): "2020-2024".
    * `@ManyToOne` `khoa`: Lớp này thuộc khoa nào.
    * `@OneToMany` `dsSinhVien`: Danh sách sinh viên thuộc lớp này.

4.  **`SinhVien.java` (Student)** - (Sửa lại từ bản gốc)

    * `maSV` (String, PK).
    * ... (Thông tin cá nhân: `hoTen`, `ngaySinh`, `gioiTinh`...).
    * `@ManyToOne` `lopHoc`: Sinh viên này thuộc lớp hành chính nào.
    * `@OneToOne(mappedBy="sinhVien")` `taiKhoan`: Tài khoản đăng nhập của SV.
    * `@OneToMany(mappedBy="sinhVien")` `dsDiem`: Danh sách các điểm của sinh viên.

5.  **`GiangVien.java` (Lecturer)**

    * `maGV` (String, PK).
    * `hoTen` (String), `hocVi` (String): "Thạc sĩ", "Tiến sĩ".
    * `@ManyToOne` `khoa`: Giảng viên này thuộc khoa nào.
    * `@OneToOne(mappedBy="giangVien")` `taiKhoan`: Tài khoản đăng nhập của GV.
    * `@OneToMany(mappedBy="giangVien")` `dsLopHocPhan`: Các lớp học phần GV này dạy.

**B. Nhóm Học vụ & Đào tạo**

6.  **`MonHoc.java` (Course/Subject)**

    * `maMH` (String, PK): "IT1110".
    * `tenMH` (String): "Lập trình Java".
    * `soTinChi` (int): Số tín chỉ (credits).
    * `@ManyToOne` `khoa`: Môn học này do khoa nào quản lý.
    * `@OneToMany(mappedBy="monHoc")` `dsLopHocPhan`: Các lớp học phần được mở từ môn này.

7.  **`HocKy.java` (Semester)**

    * `maHocKy` (String, PK): "HK1\_2024\_2025".
    * `tenHocKy` (String): "Học kỳ 1 năm học 2024-2025".
    * `ngayBatDau` (LocalDate), `ngayKetThuc` (LocalDate).
    * `@OneToMany(mappedBy="hocKy")` `dsLopHocPhan`: Các lớp học phần mở trong kỳ này.

8.  **`LopHocPhan.java` (Course Section)** - Lớp tín chỉ.

    * `maLHP` (long, PK, Auto-increment): ID duy nhất của lớp tín chỉ.
    * `@ManyToOne` `monHoc`: Lớp này của môn học nào.
    * `@ManyToOne` `giangVien`: Giảng viên nào dạy.
    * `@ManyToOne` `hocKy`: Mở trong học kỳ nào.
    * `soLuongToiDa` (int): Sĩ số tối đa.
    * `@OneToMany(mappedBy="lopHocPhan")` `dsDiem`: Bảng điểm của lớp này.

9.  **`Diem.java` (Grade)** - Bảng N-N giữa `SinhVien` và `LopHocPhan`.

    * **Bắt buộc dùng Composite Key (Khóa phức hợp)**.
    * **`@EmbeddedId` `DiemId id;`**
    * `@ManyToOne @MapsId("maSV")` `private SinhVien sinhVien;`
    * `@ManyToOne @MapsId("maLHP")` `private LopHocPhan lopHocPhan;`
    * `diemChuyenCan` (double), `diemGiuaKy` (double), `diemCuoiKy` (double).
    * `diemTongKet` (double): Điểm tổng kết (có thể được tính tự động).
    * `trangThai` (Enum): "Đạt", "Không Đạt".

10. **`DiemId.java` (Embeddable Class for Composite Key)**

    * `@Embeddable`
    * `private String maSV;`
    * `private long maLHP;`
    * (Implement `hashCode` và `equals` cẩn thận).

#### 2.2. Giao thức (Protocol) Mở rộng

**`Command.java` (Enum):** Phải được mở rộng cực lớn.

```java
public enum Command {
    // 1. Xác thực
    LOGIN,
    LOGOUT,
    
    // 2. Quản lý Khoa
    KHOA_ADD, KHOA_UPDATE, KHOA_DELETE, KHOA_GET_BY_ID, KHOA_GET_ALL,
    
    // 3. Quản lý Lớp (hành chính)
    LOPHOC_ADD, LOPHOC_UPDATE, LOPHOC_DELETE, LOPHOC_GET_ALL_BY_KHOA,
    
    // 4. Quản lý Sinh viên
    SINHVIEN_ADD, SINHVIEN_UPDATE, SINHVIEN_DELETE, SINHVIEN_GET_BY_ID,
    SINHVIEN_GET_ALL_BY_LOPHOC,
    
    // 5. Quản lý Giảng viên
    GIANGVIEN_ADD, GIANGVIEN_UPDATE, GIANGVIEN_DELETE, GIANGVIEN_GET_ALL_BY_KHOA,
    
    // 6. Quản lý Môn học
    MONHOC_ADD, MONHOC_UPDATE, MONHOC_DELETE, MONHOC_GET_ALL_BY_KHOA,

    // 7. Quản lý Đào tạo (Mở lớp tín chỉ)
    LOPHOCPHAN_ADD, LOPHOCPHAN_UPDATE, LOPHOCPHAN_DELETE,
    LOPHOCPHAN_GET_ALL_BY_HOCKY,

    // 8. Nghiệp vụ Sinh viên
    SV_GET_LOPHOCPHAN_TO_REGISTER, // Lấy ds LHP để đăng ký
    SV_REGISTER_COURSE,            // Đăng ký học phần
    SV_GET_TRANSCRIPT,             // Lấy bảng điểm
    
    // 9. Nghiệp vụ Giảng viên
    GV_GET_MY_CLASSES,             // Lấy các lớp GV dạy
    GV_GET_STUDENT_LIST_OF_CLASS,  // Lấy ds SV của 1 LHP
    GV_ENTER_GRADE                 // Nhập điểm
}
```

**`Request.java`:**

* `private Command command;`
* `private Object data;`
* `private String authToken;` // **MỚI:** Token xác thực (gửi kèm mỗi request SAU KHI login).

**`Response.java`:**

* `private Status status;` // SUCCESS, ERROR, UNAUTHORIZED (Chưa login), FORBIDDEN (Không có quyền)
* `private String message;`
* `private Object data;` // Có thể chứa Token (khi login), List\<KhoaDTO\>, BangDiemDTO, v.v.

-----

### 3\. Module `server-app` (Logic nghiệp vụ)

#### 3.1. Cấu hình `persistence.xml`

* Phải khai báo **TẤT CẢ** các Entity mới trong file `persistence.xml` (tổng cộng 10-11 lớp).

#### 3.2. Cấu trúc Gói Dịch vụ (Service Package)

Không thể dùng một `StudentService.java` duy nhất. Cần chia nhỏ:

* `com.example.server.service.AuthService`: Xử lý `LOGIN`, `LOGOUT`.
    * `login(username, password)`: Kiểm tra `TaiKhoan`, dùng `BCrypt.checkpw()` để so sánh mật khẩu hash. Nếu đúng, tạo một `authToken` (ví dụ: JWT hoặc UUID đơn giản) và lưu lại (ví dụ: trong một `ConcurrentHashMap<String, String> (token, username)`), trả `Response` kèm token.
* `com.example.server.service.KhoaService`: CRUD cho Khoa.
* `com.example.server.service.LopHocService`: CRUD cho Lớp học (hành chính).
* `com.example.server.service.SinhVienService`: CRUD cho Sinh viên.
* `com.example.server.service.GiangVienService`: CRUD cho Giảng viên.
* `com.example.server.service.HocVuService`: **Quan trọng nhất.**
    * `moLopHocPhan(LopHocPhan lhp, String maGV, String maMH, String maHocKy)`: Thêm một `LopHocPhan`.
    * `dangKyHocPhan(String maSV, long maLHP)`: Xử lý nghiệp vụ (kiểm tra sĩ số, kiểm tra trùng lịch...).
    * `nhapDiem(String maGV, Diem diem)`: Xử lý nghiệp vụ (kiểm tra GV có dạy lớp này không).
    * `xemBangDiem(String maSV)`: Lấy toàn bộ `dsDiem` của SV và tổng hợp thành `BangDiemDTO`.

#### 3.3. Lớp `ClientHandler.java` (Cập nhật)

1.  `ClientHandler` phải lưu trữ `TaiKhoan` (hoặc `username` và `role`) của client *sau khi* họ đăng nhập thành công.
2.  Hàm `handleRequest(Request req)`:
    * **Bước 1:** Nếu `command == LOGIN`, gọi `AuthService.login()` và trả kết quả.
    * **Bước 2:** Nếu `command != LOGIN`, kiểm tra `req.getAuthToken()`:
        * Token có hợp lệ không? (Tra cứu trong `AuthService`). Nếu không, trả `Response(Status.UNAUTHORIZED)`.
    * **Bước 3:** Lấy `role` của user từ token.
    * **Bước 4:** Thực hiện **Kiểm tra Phân quyền (Authorization)**.
        * Ví dụ: `case KHOA_ADD:`
        * `if (user.getRole() != UserRole.ADMIN) { return new Response(Status.FORBIDDEN); }`
        * `return khoaService.addKhoa((KhoaDTO) req.getData());`
    * **Bước 5:** Đối với các nghiệp vụ phức tạp:
        * `case SV_REGISTER_COURSE:`
        * `if (user.getRole() != UserRole.SINH_VIEN) { return new Response(Status.FORBIDDEN); }`
        * `String maSV = user.getMaSV();` // Lấy mã SV từ user đang login
        * `long maLHP = (long) req.getData();`
        * `return hocVuService.dangKyHocPhan(maSV, maLHP);`

#### 3.4. Vấn đề DTO (Data Transfer Object)

* **BẮT BUỘC SỬ DỤNG DTO.**
* **Lý do:** Nếu Client gọi `KHOA_GET_BY_ID("CNTT")`, và Server trả về Entity `Khoa`, JPA (Hibernate) có thể cố gắng tải *toàn bộ* `dsGiangVien`, `dsLopHoc`, `dsMonHoc`... (gây lỗi `LazyInitializationException` hoặc truyền hàng ngàn đối tượng qua mạng).
* **Giải pháp:** Trong `common-lib`, tạo các lớp DTO (POJO đơn giản, `Serializable`).
    * `KhoaDTO.java` (chỉ `maKhoa`, `tenKhoa`).
    * `SinhVienDTO.java` (chỉ thông tin SV, và `maLop` thay vì cả object `LopHoc`).
    * `BangDiemDTO.java` (chứa `List<DiemChiTietDTO>` và `double gpaTongKet`).
* Server sẽ có các "Mapper" để chuyển đổi giữa Entity và DTO trước khi gửi về Client.

-----

### 4\. Module `client-app` (Giao diện Admin)

Giao diện Console (mô tả trong file gốc) giờ đây trở nên cực kỳ phức tạp.

1.  **Luồng chính:**

    * Khởi động: Yêu cầu `login()`.
    * `Client.java` lưu `authToken` nhận về.
    * Mọi `sendRequest()` sau đó đều phải đính kèm token này.

2.  **Menu chính (phân cấp):**

    ```
    --- HỆ THỐNG QUẢN LÝ ĐÀO TẠO (Đăng nhập: admin) ---
    1. Quản lý Danh mục Khoa
    2. Quản lý Danh mục Lớp (theo Khoa)
    3. Quản lý Danh mục Môn học (theo Khoa)
    4. Quản lý Giảng viên (theo Khoa)
    5. Quản lý Sinh viên (theo Lớp)
    6. Quản lý Đào tạo (Mở lớp học phần)
    7. Quản lý Tài khoản
    0. Đăng xuất
    ```

3.  **Menu con (Ví dụ: chọn 1. Quản lý Khoa):**

    ```
    --- QUẢN LÝ KHOA ---
    1. Thêm Khoa mới
    2. Cập nhật Khoa
    3. Xóa Khoa
    4. Xem danh sách Khoa
    5. Quay lại
    ```

<!-- end list -->

* **Khuyến nghị:** Với độ phức tạp này, giao diện Console không còn phù hợp. Đây là lúc **bắt buộc** phải chuyển sang **Java Swing (JFrame)** như yêu cầu trước của bạn. Giao diện Swing sẽ sử dụng `JTabbedPane` (các tab cho Khoa, Lớp, SV...) để quản lý các chức năng này một cách trực quan.