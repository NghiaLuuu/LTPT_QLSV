package iuh.fit.se.gui.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.se.dto.response.LecturerResponse;
import iuh.fit.se.gui.component.ModernButton;
import iuh.fit.se.gui.component.ModernTextField;
import iuh.fit.se.gui.util.ApiClient;
import iuh.fit.se.gui.util.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Dialog thêm/sửa Giảng viên
 */
public class LecturerDialog extends JDialog {

    private ModernTextField txtLecturerCode;
    private ModernTextField txtFullName;
    private JComboBox<String> cboGender;
    private JSpinner spinnerDob;
    private ModernTextField txtEmail;
    private ModernTextField txtPhone;
    private JComboBox<FacultyItem> cboFaculty;
    private JComboBox<String> cboDegree;

    private ModernButton btnSave;
    private ModernButton btnCancel;

    private LecturerResponse lecturer;
    private boolean confirmed = false;

    // Inner class để lưu thông tin khoa
    private static class FacultyItem {
        private Long id;
        private String name;

        public FacultyItem(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public LecturerDialog(JFrame parent, String title, LecturerResponse lecturer) {
        super(parent, title, true);
        this.lecturer = lecturer;

        initComponents();

        if (lecturer != null) {
            loadLecturerData();
        }

        setSize(500, 650);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title
        JLabel lblTitle = new JLabel(lecturer == null ? "Thêm Giảng viên mới" : "Sửa thông tin Giảng viên");
        lblTitle.setFont(AppTheme.TITLE_FONT);
        lblTitle.setForeground(AppTheme.PRIMARY_COLOR);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(lblTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Lecturer Code field
        JLabel lblLecturerCode = new JLabel("Mã giảng viên *");
        lblLecturerCode.setFont(AppTheme.NORMAL_FONT);
        lblLecturerCode.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtLecturerCode = new ModernTextField(25);
        txtLecturerCode.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));
        txtLecturerCode.setEditable(false); // Không cho sửa mã
        txtLecturerCode.setBackground(new Color(240, 240, 240)); // Màu xám nhạt

        // Nếu thêm mới, tự động sinh mã
        if (lecturer == null) {
            fetchNextLecturerCode();
        }

        mainPanel.add(lblLecturerCode);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(txtLecturerCode);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Full Name field
        JLabel lblFullName = new JLabel("Họ và tên *");
        lblFullName.setFont(AppTheme.NORMAL_FONT);
        lblFullName.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtFullName = new ModernTextField(25);
        txtFullName.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));

        mainPanel.add(lblFullName);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(txtFullName);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Gender field
        JLabel lblGender = new JLabel("Giới tính *");
        lblGender.setFont(AppTheme.NORMAL_FONT);
        lblGender.setAlignmentX(Component.LEFT_ALIGNMENT);

        cboGender = new JComboBox<>(new String[]{"MALE", "FEMALE", "OTHER"});
        cboGender.setFont(AppTheme.NORMAL_FONT);
        cboGender.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));

        mainPanel.add(lblGender);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(cboGender);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // DOB field
        JLabel lblDob = new JLabel("Ngày sinh");
        lblDob.setFont(AppTheme.NORMAL_FONT);
        lblDob.setAlignmentX(Component.LEFT_ALIGNMENT);

        SpinnerDateModel dateModel = new SpinnerDateModel();
        spinnerDob = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerDob, "dd/MM/yyyy");
        spinnerDob.setEditor(dateEditor);
        spinnerDob.setFont(AppTheme.NORMAL_FONT);
        spinnerDob.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));

        mainPanel.add(lblDob);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(spinnerDob);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Email field
        JLabel lblEmail = new JLabel("Email *");
        lblEmail.setFont(AppTheme.NORMAL_FONT);
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtEmail = new ModernTextField(25);
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));

        mainPanel.add(lblEmail);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(txtEmail);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Phone field
        JLabel lblPhone = new JLabel("Số điện thoại *");
        lblPhone.setFont(AppTheme.NORMAL_FONT);
        lblPhone.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPhone = new ModernTextField(25);
        txtPhone.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));

        mainPanel.add(lblPhone);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(txtPhone);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Faculty field
        JLabel lblFaculty = new JLabel("Khoa *");
        lblFaculty.setFont(AppTheme.NORMAL_FONT);
        lblFaculty.setAlignmentX(Component.LEFT_ALIGNMENT);

        cboFaculty = new JComboBox<>();
        cboFaculty.setFont(AppTheme.NORMAL_FONT);
        cboFaculty.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));
        // Load danh sách khoa từ API
        loadFaculties();

        mainPanel.add(lblFaculty);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(cboFaculty);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Degree field
        JLabel lblDegree = new JLabel("Học vị *");
        lblDegree.setFont(AppTheme.NORMAL_FONT);
        lblDegree.setAlignmentX(Component.LEFT_ALIGNMENT);

        cboDegree = new JComboBox<>(new String[]{"Tiến sĩ", "Thạc sĩ", "Cử nhân", "Kỹ sư"});
        cboDegree.setFont(AppTheme.NORMAL_FONT);
        cboDegree.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));

        mainPanel.add(lblDegree);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(cboDegree);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        btnCancel = new ModernButton("Hủy");
        btnCancel.setBackground(AppTheme.SECONDARY_COLOR);
        btnCancel.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        btnSave = new ModernButton("Lưu");
        btnSave.setBackground(AppTheme.SUCCESS_COLOR);
        btnSave.addActionListener(e -> saveLecturer());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadFaculties() {
        SwingWorker<List<FacultyItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<FacultyItem> doInBackground() throws Exception {
                List<FacultyItem> faculties = new ArrayList<>();
                String json = ApiClient.get("/faculties");
                com.fasterxml.jackson.databind.JsonNode root = ApiClient.getObjectMapper().readTree(json);

                if (root.isArray()) {
                    for (com.fasterxml.jackson.databind.JsonNode node : root) {
                        Long id = node.get("id").asLong();
                        String name = node.get("name").asText();
                        faculties.add(new FacultyItem(id, name));
                    }
                }
                return faculties;
            }

            @Override
            protected void done() {
                try {
                    List<FacultyItem> faculties = get();
                    // Thêm placeholder item
                    cboFaculty.addItem(new FacultyItem(null, "-- Chọn khoa --"));
                    for (FacultyItem faculty : faculties) {
                        cboFaculty.addItem(faculty);
                    }
                } catch (Exception ex) {
                    System.err.println("Lỗi khi tải danh sách khoa: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void loadLecturerData() {
        txtLecturerCode.setText(lecturer.getLecturerCode());
        txtFullName.setText(lecturer.getFullName());
        cboGender.setSelectedItem(lecturer.getGender());

        if (lecturer.getDob() != null) {
            Date date = Date.from(lecturer.getDob().atStartOfDay(ZoneId.systemDefault()).toInstant());
            spinnerDob.setValue(date);
        }

        txtEmail.setText(lecturer.getEmail());
        txtPhone.setText(lecturer.getPhone());
        cboDegree.setSelectedItem(lecturer.getDegree());

        // Chọn khoa nếu có
        if (lecturer.getFaculty() != null) {
            // Chờ load xong danh sách khoa rồi mới chọn
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Thread.sleep(500); // Đợi load danh sách khoa
                    return null;
                }

                @Override
                protected void done() {
                    String facultyName = lecturer.getFaculty();
                    for (int i = 0; i < cboFaculty.getItemCount(); i++) {
                        FacultyItem item = cboFaculty.getItemAt(i);
                        if (item.getName().equals(facultyName)) {
                            cboFaculty.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            };
            worker.execute();
        }
    }

    private void saveLecturer() {
        // Validate
        String lecturerCode = txtLecturerCode.getText().trim();
        String fullName = txtFullName.getText().trim();
        String gender = (String) cboGender.getSelectedItem();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        FacultyItem selectedFaculty = (FacultyItem) cboFaculty.getSelectedItem();
        String degree = (String) cboDegree.getSelectedItem();

        if (lecturerCode.isEmpty()) {
            showError("Vui lòng nhập mã giảng viên!");
            txtLecturerCode.requestFocus();
            return;
        }

        if (fullName.isEmpty()) {
            showError("Vui lòng nhập họ tên!");
            txtFullName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            showError("Vui lòng nhập email!");
            txtEmail.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            showError("Vui lòng nhập số điện thoại!");
            txtPhone.requestFocus();
            return;
        }

        if (selectedFaculty == null || selectedFaculty.getId() == null) {
            showError("Vui lòng chọn khoa!");
            cboFaculty.requestFocus();
            return;
        }

        // Get DOB
        Date date = (Date) spinnerDob.getValue();
        LocalDate dob = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Prepare request data
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("lecturerCode", lecturerCode);
        requestData.put("fullName", fullName);
        requestData.put("gender", gender);
        requestData.put("dob", dob.toString());
        requestData.put("email", email);
        requestData.put("phone", phone);
        requestData.put("faculty", selectedFaculty.getName()); // Sửa: gửi tên khoa thay vì ID
        requestData.put("degree", degree);

        // Call API
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                ObjectMapper mapper = ApiClient.getObjectMapper();
                String jsonData = mapper.writeValueAsString(requestData);

                if (lecturer == null) {
                    // Create new
                    ApiClient.post("/lecturers", jsonData);
                } else {
                    // Update
                    ApiClient.put("/lecturers/" + lecturer.getId(), jsonData);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    confirmed = true;
                    JOptionPane.showMessageDialog(LecturerDialog.this,
                            lecturer == null ? "Thêm giảng viên thành công!" : "Cập nhật giảng viên thành công!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (Exception ex) {
                    showError("Lỗi: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void fetchNextLecturerCode() {
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                String json = ApiClient.get("/lecturers");
                com.fasterxml.jackson.databind.JsonNode root = ApiClient.getObjectMapper().readTree(json);
                int count = root.size();
                return String.format("GV%08d", count + 1);
            }

            @Override
            protected void done() {
                try {
                    String code = get();
                    txtLecturerCode.setText(code);
                } catch (Exception ex) {
                    txtLecturerCode.setText("GV00000001");
                }
            }
        };
        worker.execute();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
