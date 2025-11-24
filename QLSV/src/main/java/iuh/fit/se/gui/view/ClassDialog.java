package iuh.fit.se.gui.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.se.gui.component.ModernButton;
import iuh.fit.se.gui.component.ModernTextField;
import iuh.fit.se.gui.util.ApiClient;
import iuh.fit.se.gui.util.AppTheme;
import iuh.fit.se.model.Class;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Dialog thêm/sửa Lớp học
 */
public class ClassDialog extends JDialog {

    private ModernTextField txtName;
    private JComboBox<FacultyItem> cboFaculty;
    private JSpinner spinnerCourseYear;

    private ModernButton btnSave;
    private ModernButton btnCancel;

    private Class classData;
    private boolean confirmed = false;

    public ClassDialog(Window parent, Class clazz) {
        super(parent, "Quản lý Lớp học", ModalityType.APPLICATION_MODAL);
        this.classData = clazz;
        initComponents();
        if (clazz != null) {
            loadClassData();
        }
        pack();
        setLocationRelativeTo(parent);
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


    public ClassDialog(JFrame parent, String title, Class classData) {
        super(parent, title, true);
        this.classData = classData;

        initComponents();

        if (classData != null) {
            loadClassData();
        }

        setSize(450, 400);
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
        JLabel lblTitle = new JLabel(classData == null ? "Thêm Lớp học mới" : "Sửa thông tin Lớp học");
        lblTitle.setFont(AppTheme.TITLE_FONT);
        lblTitle.setForeground(AppTheme.PRIMARY_COLOR);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(lblTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Name field
        JLabel lblName = new JLabel("Tên lớp học *");
        lblName.setFont(AppTheme.NORMAL_FONT);
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtName = new ModernTextField(25);
        txtName.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));

        mainPanel.add(lblName);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(txtName);
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

        // Course year field
        JLabel lblCourseYear = new JLabel("Khóa học *");
        lblCourseYear.setFont(AppTheme.NORMAL_FONT);
        lblCourseYear.setAlignmentX(Component.LEFT_ALIGNMENT);

        SpinnerNumberModel yearModel = new SpinnerNumberModel(2024, 2000, 2100, 1);
        spinnerCourseYear = new JSpinner(yearModel);
        spinnerCourseYear.setFont(AppTheme.NORMAL_FONT);
        spinnerCourseYear.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));

        mainPanel.add(lblCourseYear);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(spinnerCourseYear);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        add(mainPanel, BorderLayout.CENTER);

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
        btnSave.addActionListener(e -> saveClass());

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

    private void loadClassData() {
        if (classData == null) return;
        txtName.setText(classData.getName());
        spinnerCourseYear.setValue(classData.getCourseYear());

        // Chọn khoa nếu có
        if (classData.getFaculty() != null) {
            // Chờ load xong danh sách khoa rồi mới chọn
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Thread.sleep(500); // Đợi load danh sách khoa
                    return null;
                }

                @Override
                protected void done() {
                    for (int i = 0; i < cboFaculty.getItemCount(); i++) {
                        FacultyItem item = cboFaculty.getItemAt(i);
                        if (item.getId().equals(classData.getFaculty().getId())) {
                            cboFaculty.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            };
            worker.execute();
        }
    }

    private void saveClass() {
        // Validate
        String name = txtName.getText().trim();
        FacultyItem selectedFaculty = (FacultyItem) cboFaculty.getSelectedItem();
        Integer courseYear = (Integer) spinnerCourseYear.getValue();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tên lớp học!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            txtName.requestFocus();
            return;
        }

        if (selectedFaculty == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn khoa!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            cboFaculty.requestFocus();
            return;
        }

        // Prepare request data
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("name", name);
        requestData.put("facultyId", selectedFaculty.getId());
        requestData.put("courseYear", courseYear);

        // Call API
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                ObjectMapper mapper = ApiClient.getObjectMapper();
                String jsonData = mapper.writeValueAsString(requestData);

                if (classData == null) {
                    // Create new
                    ApiClient.post("/classes", jsonData);
                } else {
                    // Update
                    ApiClient.put("/classes/" + classData.getId(), jsonData);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    confirmed = true;
                    JOptionPane.showMessageDialog(ClassDialog.this,
                            classData == null ? "Thêm lớp học thành công!" : "Cập nhật lớp học thành công!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ClassDialog.this,
                            "Lỗi: " + ex.getMessage(),
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
