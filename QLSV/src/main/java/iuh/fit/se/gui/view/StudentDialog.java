package iuh.fit.se.gui.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.se.dto.response.StudentResponse;
import iuh.fit.se.gui.component.ModernButton;
import iuh.fit.se.gui.component.ModernTextField;
import iuh.fit.se.gui.util.ApiClient;
import iuh.fit.se.gui.util.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Dialog thêm/sửa Sinh viên
 */
public class StudentDialog extends JDialog {

    private ModernTextField txtStudentCode;
    private ModernTextField txtFullName;
    private JComboBox<String> cboGender;
    private JSpinner spinnerDob;
    private ModernTextField txtEmail;
    private JComboBox<ClassItem> cboClass;
    private JComboBox<FacultyItem> cboFaculty;

    private ModernButton btnSave;
    private ModernButton btnCancel;

    private StudentResponse student;
    private boolean confirmed = false;

    // Inner class để lưu thông tin lớp
    private static class ClassItem {
        private Long id;
        private String name;

        public ClassItem(Long id, String name) {
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

    public StudentDialog(JFrame parent, String title, StudentResponse student) {
        super(parent, title, true);
        this.student = student;

        initComponents();

        if (student != null) {
            loadStudentData();
        }

        setSize(500, 650);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        contentPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;

        // Mã sinh viên
        JLabel lblStudentCode = new JLabel("Mã sinh viên:");
        lblStudentCode.setFont(AppTheme.NORMAL_FONT);
        contentPanel.add(lblStudentCode, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtStudentCode = new ModernTextField(20);
        txtStudentCode.setEditable(false);
        txtStudentCode.setBackground(new Color(240, 240, 240));

        if (student == null) {
            fetchNextStudentCode();
        }
        contentPanel.add(txtStudentCode, gbc);

        // Họ tên
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lblFullName = new JLabel("Họ tên:");
        lblFullName.setFont(AppTheme.NORMAL_FONT);
        contentPanel.add(lblFullName, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtFullName = new ModernTextField(20);
        contentPanel.add(txtFullName, gbc);

        // Giới tính
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel lblGender = new JLabel("Giới tính:");
        lblGender.setFont(AppTheme.NORMAL_FONT);
        contentPanel.add(lblGender, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        cboGender = new JComboBox<>(new String[]{"MALE", "FEMALE", "OTHER"});
        cboGender.setFont(AppTheme.NORMAL_FONT);
        contentPanel.add(cboGender, gbc);

        // Ngày sinh
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel lblDob = new JLabel("Ngày sinh:");
        lblDob.setFont(AppTheme.NORMAL_FONT);
        contentPanel.add(lblDob, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        SpinnerDateModel dateModel = new SpinnerDateModel();
        spinnerDob = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerDob, "dd/MM/yyyy");
        spinnerDob.setEditor(dateEditor);
        spinnerDob.setFont(AppTheme.NORMAL_FONT);
        contentPanel.add(spinnerDob, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(AppTheme.NORMAL_FONT);
        contentPanel.add(lblEmail, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtEmail = new ModernTextField(20);
        contentPanel.add(txtEmail, gbc);

        // Khoa
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.3;
        JLabel lblFaculty = new JLabel("Khoa:");
        lblFaculty.setFont(AppTheme.NORMAL_FONT);
        contentPanel.add(lblFaculty, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        cboFaculty = new JComboBox<>();
        cboFaculty.setFont(AppTheme.NORMAL_FONT);
        cboFaculty.addItem(new FacultyItem(null, "Chưa có khoa"));
        loadFaculties();
        contentPanel.add(cboFaculty, gbc);

        // Lớp
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.3;
        JLabel lblClass = new JLabel("Lớp:");
        lblClass.setFont(AppTheme.NORMAL_FONT);
        contentPanel.add(lblClass, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        cboClass = new JComboBox<>();
        cboClass.setFont(AppTheme.NORMAL_FONT);
        cboClass.addItem(new ClassItem(null, "Chưa có lớp"));
        loadClasses();
        contentPanel.add(cboClass, gbc);

        add(contentPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setBackground(Color.WHITE);

        btnCancel = new ModernButton("Hủy");
        btnCancel.setBackground(AppTheme.TEXT_SECONDARY);
        btnCancel.addActionListener(e -> dispose());

        btnSave = new ModernButton("Lưu");
        btnSave.setBackground(AppTheme.SUCCESS_COLOR);
        btnSave.addActionListener(e -> saveStudent());

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
                    for (FacultyItem faculty : faculties) {
                        cboFaculty.addItem(faculty);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void loadClasses() {
        SwingWorker<List<ClassItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ClassItem> doInBackground() throws Exception {
                List<ClassItem> classes = new ArrayList<>();
                String json = ApiClient.get("/classes");
                com.fasterxml.jackson.databind.JsonNode root = ApiClient.getObjectMapper().readTree(json);

                if (root.isArray()) {
                    for (com.fasterxml.jackson.databind.JsonNode node : root) {
                        Long id = node.get("id").asLong();
                        String name = node.get("name").asText();
                        classes.add(new ClassItem(id, name));
                    }
                }
                return classes;
            }

            @Override
            protected void done() {
                try {
                    List<ClassItem> classes = get();
                    for (ClassItem classItem : classes) {
                        cboClass.addItem(classItem);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void fetchNextStudentCode() {
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                String json = ApiClient.get("/students");
                // Parse để đếm số lượng
                com.fasterxml.jackson.databind.JsonNode root = ApiClient.getObjectMapper().readTree(json);
                int count = root.size();
                return String.format("SV%08d", count + 1);
            }

            @Override
            protected void done() {
                try {
                    String code = get();
                    txtStudentCode.setText(code);
                } catch (Exception ex) {
                    txtStudentCode.setText("SV00000001");
                }
            }
        };
        worker.execute();
    }

    private void loadStudentData() {
        txtStudentCode.setText(student.getStudentCode());
        txtFullName.setText(student.getFullName());
        cboGender.setSelectedItem(student.getGender().toString());
        txtEmail.setText(student.getEmail());

        // Set date
        java.util.Date date = java.sql.Date.valueOf(student.getDob());
        spinnerDob.setValue(date);

        // Chọn khoa nếu có
        if (student.getFacultyId() != null) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Thread.sleep(500);
                    return null;
                }

                @Override
                protected void done() {
                    for (int i = 0; i < cboFaculty.getItemCount(); i++) {
                        FacultyItem item = cboFaculty.getItemAt(i);
                        if (item.getId() != null && item.getId().equals(student.getFacultyId())) {
                            cboFaculty.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            };
            worker.execute();
        }

        // Chọn lớp nếu có
        if (student.getClassId() != null) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Thread.sleep(500);
                    return null;
                }

                @Override
                protected void done() {
                    for (int i = 0; i < cboClass.getItemCount(); i++) {
                        ClassItem item = cboClass.getItemAt(i);
                        if (item.getId() != null && item.getId().equals(student.getClassId())) {
                            cboClass.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            };
            worker.execute();
        }
    }

    private void saveStudent() {
        // Validate
        if (txtStudentCode.getText().trim().isEmpty() ||
                txtFullName.getText().trim().isEmpty() ||
                txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ thông tin!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText("Đang lưu...");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Prepare JSON
                Map<String, Object> data = new HashMap<>();
                data.put("studentCode", txtStudentCode.getText().trim());
                data.put("fullName", txtFullName.getText().trim());
                data.put("gender", cboGender.getSelectedItem().toString());

                // Convert date
                java.util.Date date = (java.util.Date) spinnerDob.getValue();
                LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate();
                data.put("dob", localDate.toString());

                data.put("email", txtEmail.getText().trim());

                // Faculty ID
                FacultyItem selectedFaculty = (FacultyItem) cboFaculty.getSelectedItem();
                if (selectedFaculty != null && selectedFaculty.getId() != null) {
                    data.put("facultyId", selectedFaculty.getId());
                }

                // Class ID
                ClassItem selectedClass = (ClassItem) cboClass.getSelectedItem();
                if (selectedClass != null && selectedClass.getId() != null) {
                    data.put("classId", selectedClass.getId());
                }

                ObjectMapper mapper = ApiClient.getObjectMapper();
                String json = mapper.writeValueAsString(data);

                if (student == null) {
                    ApiClient.post("/students", json);
                } else {
                    ApiClient.put("/students/" + student.getId(), json);
                }

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    confirmed = true;
                    JOptionPane.showMessageDialog(StudentDialog.this,
                            "Lưu thông tin sinh viên thành công!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (Exception ex) {
                    btnSave.setEnabled(true);
                    btnSave.setText("Lưu");
                    JOptionPane.showMessageDialog(StudentDialog.this,
                            "Lỗi khi lưu: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
