package iuh.fit.se.gui.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.se.gui.component.ModernButton;
import iuh.fit.se.gui.component.ModernTextField;
import iuh.fit.se.gui.util.ApiClient;
import iuh.fit.se.gui.util.AppTheme;
import iuh.fit.se.model.Subject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Dialog thêm/sửa Môn học
 */
public class SubjectDialog extends JDialog {

    private ModernTextField txtCode;
    private ModernTextField txtName;
    private JSpinner spinnerCredit;
    private JSpinner spinnerMaxStudents;
    private JComboBox<LecturerItem> cboLecturer;

    // Thêm components cho việc chọn sinh viên
    private JTable studentTable;
    private DefaultTableModel studentTableModel;
    private List<Long> selectedStudentIds = new ArrayList<>();

    private ModernButton btnSave;
    private ModernButton btnCancel;

    private Subject subject;
    private boolean confirmed = false;

    // Inner class để lưu thông tin giảng viên
    private static class LecturerItem {
        private Long id;
        private String name;

        public LecturerItem(Long id, String name) {
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

    // Inner class để lưu thông tin sinh viên
    private static class StudentItem {
        private Long id;
        private String studentCode;
        private String fullName;

        public StudentItem(Long id, String studentCode, String fullName) {
            this.id = id;
            this.studentCode = studentCode;
            this.fullName = fullName;
        }

        public Long getId() {
            return id;
        }

        public String getStudentCode() {
            return studentCode;
        }

        public String getFullName() {
            return fullName;
        }
    }

    public SubjectDialog(JFrame parent, String title, Subject subject) {
        super(parent, title, true);
        this.subject = subject;

        initComponents();

        if (subject != null) {
            loadSubjectData();
        }

        setSize(700, 750);
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
        JLabel lblTitle = new JLabel(subject == null ? "Thêm Môn học mới" : "Sửa thông tin Môn học");
        lblTitle.setFont(AppTheme.TITLE_FONT);
        lblTitle.setForeground(AppTheme.PRIMARY_COLOR);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(lblTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Code field
        JLabel lblCode = new JLabel("Mã môn học *");
        lblCode.setFont(AppTheme.NORMAL_FONT);
        lblCode.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtCode = new ModernTextField(25);
        txtCode.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));
        txtCode.setEditable(false);
        txtCode.setBackground(new Color(240, 240, 240));

        if (subject == null) {
            fetchNextSubjectCode();
        }

        mainPanel.add(lblCode);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(txtCode);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Name field
        JLabel lblName = new JLabel("Tên môn học *");
        lblName.setFont(AppTheme.NORMAL_FONT);
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtName = new ModernTextField(25);
        txtName.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));

        mainPanel.add(lblName);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(txtName);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Credit field
        JLabel lblCredit = new JLabel("Số tín chỉ *");
        lblCredit.setFont(AppTheme.NORMAL_FONT);
        lblCredit.setAlignmentX(Component.LEFT_ALIGNMENT);

        SpinnerNumberModel creditModel = new SpinnerNumberModel(3, 1, 10, 1);
        spinnerCredit = new JSpinner(creditModel);
        spinnerCredit.setFont(AppTheme.NORMAL_FONT);
        spinnerCredit.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));

        mainPanel.add(lblCredit);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(spinnerCredit);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Max students field
        JLabel lblMaxStudents = new JLabel("Số lượng sinh viên tối đa");
        lblMaxStudents.setFont(AppTheme.NORMAL_FONT);
        lblMaxStudents.setAlignmentX(Component.LEFT_ALIGNMENT);

        SpinnerNumberModel maxStudentsModel = new SpinnerNumberModel(50, 10, 200, 10);
        spinnerMaxStudents = new JSpinner(maxStudentsModel);
        spinnerMaxStudents.setFont(AppTheme.NORMAL_FONT);
        spinnerMaxStudents.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));

        mainPanel.add(lblMaxStudents);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(spinnerMaxStudents);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Lecturer field
        JLabel lblLecturer = new JLabel("Giảng viên phụ trách");
        lblLecturer.setFont(AppTheme.NORMAL_FONT);
        lblLecturer.setAlignmentX(Component.LEFT_ALIGNMENT);

        cboLecturer = new JComboBox<>();
        cboLecturer.setFont(AppTheme.NORMAL_FONT);
        cboLecturer.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));
        cboLecturer.addItem(new LecturerItem(null, "Chưa có giảng viên"));
        loadLecturers();

        mainPanel.add(lblLecturer);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(cboLecturer);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Student selection (only for edit mode)
        if (subject != null) {
            JLabel lblStudents = new JLabel("Thêm sinh viên vào môn học");
            lblStudents.setFont(AppTheme.NORMAL_FONT.deriveFont(Font.BOLD));
            lblStudents.setAlignmentX(Component.LEFT_ALIGNMENT);

            mainPanel.add(lblStudents);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            // Student table with checkboxes
            String[] columns = {"Chọn", "Mã SV", "Họ tên"};
            studentTableModel = new DefaultTableModel(columns, 0) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return columnIndex == 0 ? Boolean.class : String.class;
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 0;
                }
            };

            studentTable = new JTable(studentTableModel);
            studentTable.setFont(AppTheme.NORMAL_FONT);
            studentTable.setRowHeight(30);
            studentTable.getTableHeader().setFont(AppTheme.NORMAL_FONT);
            studentTable.getTableHeader().setBackground(AppTheme.PRIMARY_COLOR);
            studentTable.getTableHeader().setForeground(Color.WHITE);

            JScrollPane studentScrollPane = new JScrollPane(studentTable);
            studentScrollPane.setPreferredSize(new Dimension(600, 200));
            studentScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
            studentScrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR));

            mainPanel.add(studentScrollPane);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            loadAvailableStudents();
        }

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
        btnSave.addActionListener(e -> saveSubject());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadAvailableStudents() {
        SwingWorker<List<StudentItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<StudentItem> doInBackground() throws Exception {
                List<StudentItem> students = new ArrayList<>();
                String json = ApiClient.get("/students");
                com.fasterxml.jackson.databind.JsonNode root = ApiClient.getObjectMapper().readTree(json);

                if (root.isArray()) {
                    for (com.fasterxml.jackson.databind.JsonNode node : root) {
                        Long id = node.get("id").asLong();
                        String studentCode = node.get("studentCode").asText();
                        String fullName = node.get("fullName").asText();
                        students.add(new StudentItem(id, studentCode, fullName));
                    }
                }
                return students;
            }

            @Override
            protected void done() {
                try {
                    List<StudentItem> students = get();
                    for (StudentItem student : students) {
                        studentTableModel.addRow(new Object[]{
                            false,
                            student.getStudentCode(),
                            student.getFullName()
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void loadLecturers() {
        SwingWorker<List<LecturerItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<LecturerItem> doInBackground() throws Exception {
                List<LecturerItem> lecturers = new ArrayList<>();
                String json = ApiClient.get("/lecturers");
                com.fasterxml.jackson.databind.JsonNode root = ApiClient.getObjectMapper().readTree(json);

                if (root.isArray()) {
                    for (com.fasterxml.jackson.databind.JsonNode node : root) {
                        Long id = node.get("id").asLong();
                        String fullName = node.get("fullName").asText();
                        String lecturerCode = node.get("lecturerCode").asText();
                        lecturers.add(new LecturerItem(id, lecturerCode + " - " + fullName));
                    }
                }
                return lecturers;
            }

            @Override
            protected void done() {
                try {
                    List<LecturerItem> lecturers = get();
                    for (LecturerItem lecturer : lecturers) {
                        cboLecturer.addItem(lecturer);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void loadSubjectData() {
        txtCode.setText(subject.getCode());
        txtName.setText(subject.getName());
        spinnerCredit.setValue(subject.getCredit());

        if (subject.getMaxStudents() != null) {
            spinnerMaxStudents.setValue(subject.getMaxStudents());
        }

        // Chọn giảng viên nếu có
        if (subject.getLecturer() != null) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Thread.sleep(500);
                    return null;
                }

                @Override
                protected void done() {
                    Long lecturerId = subject.getLecturer().getId();
                    for (int i = 0; i < cboLecturer.getItemCount(); i++) {
                        LecturerItem item = cboLecturer.getItemAt(i);
                        if (item.getId() != null && item.getId().equals(lecturerId)) {
                            cboLecturer.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            };
            worker.execute();
        }
    }

    private void saveSubject() {
        // Validate
        String code = txtCode.getText().trim();
        String name = txtName.getText().trim();
        Integer credit = (Integer) spinnerCredit.getValue();
        Integer maxStudents = (Integer) spinnerMaxStudents.getValue();
        LecturerItem selectedLecturer = (LecturerItem) cboLecturer.getSelectedItem();

        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập mã môn học!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            txtCode.requestFocus();
            return;
        }

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tên môn học!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            txtName.requestFocus();
            return;
        }

        // Prepare request data
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("code", code);
        requestData.put("name", name);
        requestData.put("credit", credit);
        requestData.put("maxStudents", maxStudents);

        if (selectedLecturer != null && selectedLecturer.getId() != null) {
            requestData.put("lecturerId", selectedLecturer.getId());
        }

        // Thêm danh sách sinh viên được chọn (chỉ khi edit)
        if (subject != null && studentTable != null) {
            List<Long> selectedIds = new ArrayList<>();
            SwingWorker<List<StudentItem>, Void> loadStudentsWorker = new SwingWorker<>() {
                @Override
                protected List<StudentItem> doInBackground() throws Exception {
                    List<StudentItem> students = new ArrayList<>();
                    String json = ApiClient.get("/students");
                    com.fasterxml.jackson.databind.JsonNode root = ApiClient.getObjectMapper().readTree(json);

                    if (root.isArray()) {
                        for (com.fasterxml.jackson.databind.JsonNode node : root) {
                            Long id = node.get("id").asLong();
                            String studentCode = node.get("studentCode").asText();
                            String fullName = node.get("fullName").asText();
                            students.add(new StudentItem(id, studentCode, fullName));
                        }
                    }
                    return students;
                }

                @Override
                protected void done() {
                    try {
                        List<StudentItem> allStudents = get();

                        for (int i = 0; i < studentTableModel.getRowCount(); i++) {
                            Boolean isSelected = (Boolean) studentTableModel.getValueAt(i, 0);
                            if (isSelected != null && isSelected) {
                                String studentCode = (String) studentTableModel.getValueAt(i, 1);
                                for (StudentItem student : allStudents) {
                                    if (student.getStudentCode().equals(studentCode)) {
                                        selectedIds.add(student.getId());
                                        break;
                                    }
                                }
                            }
                        }

                        if (!selectedIds.isEmpty()) {
                            requestData.put("studentIds", selectedIds);
                        }

                        // Proceed with API call
                        callSaveAPI(requestData);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        callSaveAPI(requestData);
                    }
                }
            };
            loadStudentsWorker.execute();
        } else {
            callSaveAPI(requestData);
        }
    }

    private void callSaveAPI(Map<String, Object> requestData) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                ObjectMapper mapper = ApiClient.getObjectMapper();
                String jsonData = mapper.writeValueAsString(requestData);

                if (subject == null) {
                    ApiClient.post("/subjects", jsonData);
                } else {
                    ApiClient.put("/subjects/" + subject.getId(), jsonData);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    confirmed = true;
                    JOptionPane.showMessageDialog(SubjectDialog.this,
                            subject == null ? "Thêm môn học thành công!" : "Cập nhật môn học thành công!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(SubjectDialog.this,
                            "Lỗi: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void fetchNextSubjectCode() {
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                String json = ApiClient.get("/subjects");
                com.fasterxml.jackson.databind.JsonNode root = ApiClient.getObjectMapper().readTree(json);
                int count = root.size();
                return String.format("MH%08d", count + 1);
            }

            @Override
            protected void done() {
                try {
                    String code = get();
                    txtCode.setText(code);
                } catch (Exception ex) {
                    txtCode.setText("MH00000001");
                }
            }
        };
        worker.execute();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
