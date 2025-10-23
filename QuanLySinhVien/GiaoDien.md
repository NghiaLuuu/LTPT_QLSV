

## Yêu cầu Kỹ thuật: Giao diện Client Quản lý Sinh viên (Java Swing)

### 1. Mục tiêu

Xây dựng một ứng dụng Desktop (Client) có giao diện đồ họa (GUI) bằng Java Swing để thay thế cho giao diện Console. Ứng dụng này phải:
* Trực quan, dễ sử dụng.
* Thực hiện đầy đủ các chức năng CRUD (Thêm, Sửa, Xóa, Xem, Tìm kiếm) bằng cách giao tiếp với Server qua `NetworkClient` đã định nghĩa.
* Cung cấp phản hồi tức thì cho người dùng.
* Quản lý việc cập nhật UI một cách an toàn (thread-safe).

### 2. Thiết kế Giao diện Chính (MainFrame.java)

Đây là cửa sổ chính của ứng dụng.

* **Loại:** `javax.swing.JFrame`.
* **Tiêu đề:** "Chương trình Quản lý Sinh viên".
* **Layout chính:** `BorderLayout`.

#### 2.1. Khu vực Phía trên (NORTH) - Panel Tìm kiếm

* Một `JPanel` sử dụng `FlowLayout` (hoặc `GridBagLayout` tinh chỉnh).
* **Thành phần:**
    * `JLabel`: "Tìm kiếm (theo tên hoặc mã SV):"
    * `JTextField` (ví dụ: `txtSearch`): Ô để người dùng nhập từ khóa tìm kiếm.
    * `JButton` (ví dụ: `btnSearch`): Nút "Tìm" (Chức năng nâng cao sẽ là lọc tự động).

#### 2.2. Khu vực Trung tâm (CENTER) - Bảng Dữ liệu

* Đây là thành phần quan trọng nhất.
* **Thành phần:**
    * `JTable` (ví dụ: `tblStudents`): Bảng hiển thị danh sách sinh viên.
    * **Yêu cầu `JTable`:**
        * Không cho phép sửa trực tiếp trên ô (cell editable = false).
        * Chỉ cho phép chọn một hàng tại một thời điểm (`ListSelectionModel.SINGLE_SELECTION`).
        * Phải được đặt trong một `JScrollPane` để hỗ trợ cuộn.
    * **Cột (Headers):** `Mã SV`, `Họ Tên`, `Ngày Sinh`, `Giới Tính`, `Chuyên Ngành`, `Lớp`, `Điểm TB`.
* **Mô hình Dữ liệu (Bắt buộc):**
    * Phải sử dụng một `DefaultTableModel` (hoặc `AbstractTableModel` tùy chỉnh) để quản lý dữ liệu cho `JTable`.
    * Cần một hàm (ví dụ: `loadDataToTable(List<SinhVien> list)`) để xóa dữ liệu cũ trong table model và nạp dữ liệu mới từ danh sách nhận về từ Server.

#### 2.3. Khu vực Phía dưới (SOUTH) - Panel Chức năng

* Một `JPanel` sử dụng `FlowLayout`.
* **Thành phần:**
    * `JButton` (ví dụ: `btnAdd`): "Thêm Sinh viên".
    * `JButton` (ví dụ: `btnEdit`): "Sửa Sinh viên" (Nút này chỉ `setEnabled(true)` khi có một hàng đang được chọn trong `JTable`).
    * `JButton` (ví dụ: `btnDelete`): "Xóa Sinh viên" (Nút này cũng chỉ `setEnabled(true)` khi có hàng được chọn).
    * `JButton` (ví dụ: `btnRefresh`): "Làm mới Danh sách".



### 3. Thiết kế Form Nhập liệu (StudentDialog.java)

Đây là cửa sổ (dialog) bật lên khi người dùng "Thêm" hoặc "Sửa" sinh viên.

* **Loại:** `javax.swing.JDialog`.
* **Tính chất:** **Modal** (phải đóng dialog này mới được tương tác với cửa sổ chính).
* **Layout:** `GridBagLayout` (khuyến nghị cao) hoặc `SpringLayout` để căn chỉnh các `JLabel` và `JTextField` thẳng hàng, gọn gàng.
* **Thành phần Form:**
    * `JLabel` "Mã SV:" và `JTextField` `txtMaSV`.
    * `JLabel` "Họ Tên:" và `JTextField` `txtHoTen`.
    * `JLabel` "Ngày Sinh:" và **(Nâng cao)** `JDateChooser` (từ thư viện JCalendar) hoặc `JSpinner` (cho ngày, tháng, năm).
    * `JLabel` "Giới tính:" và `JComboBox` `cbGender` (chứa 3 giá trị từ enum `Gender.MALE`, `Gender.FEMALE`, `Gender.OTHER`).
    * `JLabel` "Chuyên ngành:" và `JTextField` `txtChuyenNganh`.
    * `JLabel` "Lớp:" và `JTextField` `txtLop`.
    * `JLabel` "Điểm TB:" và `JSpinner` (cho phép nhập số thực 0-10) hoặc `JTextField` (kèm validation).
* **Panel Nút bấm (dưới cùng của Dialog):**
    * `JButton` "Lưu" / "Cập nhật" (ví dụ: `btnSave`).
    * `JButton` "Hủy bỏ" (ví dụ: `btnCancel`).

### 4. Luồng Chức năng & Xử lý Sự kiện

#### 4.1. Khởi động ứng dụng (MainFrame `constructor`)

1.  Hiển thị `MainFrame`.
2.  **(Quan trọng)** Gọi hàm `loadAllStudents()` để tải dữ liệu lần đầu.

#### 4.2. Tải/Làm mới Dữ liệu (Sự kiện `btnRefresh` hoặc `loadAllStudents()`)

1.  Hiển thị thông báo "Đang tải..." (ví dụ: trên một `JLabel` trạng thái).
2.  Gửi yêu cầu `Request(Command.GET_ALL_STUDENTS, null)` đến server.
3.  Nhận `Response`.
4.  Nếu `Response.status == SUCCESS`:
    * Lấy `List<SinhVien> data = (List<SinhVien>) res.getData()`.
    * Gọi hàm `loadDataToTable(data)` để cập nhật `JTable`.
5.  Nếu `Response.status == ERROR`:
    * Hiển thị `JOptionPane.showMessageDialog()` với nội dung lỗi `res.getMessage()`.
6.  Ẩn thông báo "Đang tải...".

#### 4.3. Thêm Sinh viên (Sự kiện `btnAdd`)

1.  Tạo một `StudentDialog` mới (ở chế độ "Thêm").
2.  Hiển thị dialog (`dialog.setVisible(true)`).
3.  *Logic bên trong `StudentDialog` (sự kiện `btnSave`):*
    * Thực hiện **Validation (Kiểm tra hợp lệ)**: `maSV`, `hoTen` không được rỗng; `diemTB` phải là số hợp lệ, v.v. Nếu lỗi, hiển thị `JOptionPane` và *không* đóng dialog.
    * Nếu hợp lệ, tạo đối tượng `SinhVien svMoi` từ dữ liệu form.
    * Gửi `Request(Command.ADD_STUDENT, svMoi)` đến server.
    * Nhận `Response`.
    * Hiển thị `JOptionPane` thông báo kết quả (`res.getMessage()`).
    * Nếu `SUCCESS`, đóng dialog.
4.  *Quay lại `MainFrame`:* Sau khi dialog đóng, gọi lại `loadAllStudents()` để làm mới bảng.

#### 4.4. Sửa Sinh viên (Sự kiện `btnEdit`)

1.  Lấy hàng đang được chọn từ `JTable` (ví dụ: `tblStudents.getSelectedRow()`).
2.  Nếu không có hàng nào được chọn, bỏ qua (nút này vốn nên bị vô hiệu hóa).
3.  Lấy `maSV` từ hàng được chọn.
4.  Gửi `Request(Command.FIND_STUDENT_BY_ID, maSV)` để lấy đối tượng `SinhVien` đầy đủ (vì `JTable` có thể không hiển thị hết).
5.  Khi nhận `Response` thành công:
    * Tạo `StudentDialog` (ở chế độ "Sửa"), truyền đối tượng `SinhVien` vừa tìm được vào.
    * Dialog phải lấp đầy (populate) dữ liệu vào các ô `JTextField`, `JComboBox`...
    * **Khóa (disable) `txtMaSV`** (`txtMaSV.setEnabled(false)`), vì không được sửa khóa chính.
    * Hiển thị dialog.
6.  *Logic bên trong `StudentDialog` (sự kiện `btnSave`):*
    * Tương tự như "Thêm", thực hiện validation (trừ `maSV`).
    * Tạo đối tượng `SinhVien svCapNhat` từ form.
    * Gửi `Request(Command.UPDATE_STUDENT, svCapNhat)`.
    * Xử lý `Response` và đóng dialog nếu `SUCCESS`.
7.  *Quay lại `MainFrame`:* Gọi `loadAllStudents()` để làm mới bảng.

#### 4.5. Xóa Sinh viên (Sự kiện `btnDelete`)

1.  Lấy hàng đang được chọn và `maSV` từ `JTable`.
2.  Hiển thị `JOptionPane.showConfirmDialog()`: "Bạn có chắc chắn muốn xóa sinh viên [Tên SV]?"
3.  Nếu người dùng chọn "Yes":
    * Gửi `Request(Command.DELETE_STUDENT, maSV)`.
    * Nhận `Response` và hiển thị `JOptionPane` (thành công hoặc thất bại).
    * Nếu `SUCCESS`, gọi `loadAllStudents()` để làm mới bảng.

#### 4.6. Tìm kiếm (Sự kiện `txtSearch`) - (Nâng cao)

1.  Sử dụng `TableRowSorter` cho `JTable`.
2.  Thêm một `DocumentListener` vào `txtSearch`.
3.  Bất cứ khi nào văn bản trong `txtSearch` thay đổi (sự kiện `changedUpdate`, `insertUpdate`, `removeUpdate`):
    * Lấy nội dung `text = txtSearch.getText()`.
    * Tạo một `RowFilter` (ví dụ: `RowFilter.regexFilter("(?i)" + text)`) – `(?i)` để không phân biệt hoa thường.
    * Set filter này cho `TableRowSorter`: `sorter.setRowFilter(filter)`.
4.  **Kết quả:** `JTable` sẽ tự động được lọc *phía client* khi người dùng gõ.

### 5. Yêu cầu Kỹ thuật Nâng cao (Bắt buộc)

1.  **Xử lý Đa luồng (Thread Safety):**
    * Tất cả các lệnh gọi mạng (`networkClient.sendRequest(...)`) **KHÔNG** được thực hiện trên **Event Dispatch Thread (EDT)** (luồng chính của Swing).
    * **Yêu cầu:** Sử dụng `javax.swing.SwingWorker` cho *mọi* tác vụ giao tiếp với server (Add, Edit, Delete, Load All).
    * **Luồng (Flow) với `SwingWorker`:**
        * `doInBackground()`: Thực hiện `networkClient.sendRequest()` và trả về `Response`.
        * `done()`: Lấy `Response` từ `get()`, sau đó cập nhật UI (ví dụ: `loadDataToTable()` hoặc hiển thị `JOptionPane`).
    * Điều này ngăn chặn UI bị "đơ" (freeze) trong khi chờ server phản hồi.

2.  **Cải thiện Giao diện (Look and Feel):**
    * Khuyến khích sử dụng một Look and Feel (L&F) hiện đại thay vì L&F "Metal" mặc định của Java.
    * **Đề xuất:** **FlatLaf** (thư viện bên ngoài, gọn nhẹ, hiện đại) hoặc ít nhất là L&F `Nimbus` có sẵn của Swing.
    * Code để set L&F phải được gọi *trước khi* `JFrame` được tạo (ví dụ: trong `main()`).

3.  **Xử lý Ngoại lệ & Mất kết nối:**
    * Hàm `networkClient.connect()` (khi khởi động) phải được bọc trong `try-catch`. Nếu thất bại, hiển thị `JOptionPane` lỗi nghiêm trọng và thoát ứng dụng.
    * Nếu `networkClient.sendRequest()` ném ra `Exception` (ví dụ: `SocketException` do server sập), `SwingWorker` phải bắt được lỗi này và `done()` phải hiển thị `JOptionPane` thông báo "Mất kết nối đến server".