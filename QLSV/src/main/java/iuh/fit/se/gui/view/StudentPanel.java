package iuh.fit.se.gui.view;

import com.fasterxml.jackson.core.type.TypeReference;
import iuh.fit.se.dto.response.StudentResponse;
import iuh.fit.se.gui.component.ModernButton;
import iuh.fit.se.gui.component.ModernTextField;
import iuh.fit.se.gui.util.ApiClient;
import iuh.fit.se.gui.util.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel quản lý Sinh viên với bảng dữ liệu
 */
public class StudentPanel extends JPanel {

    private JTable studentTable;
    private DefaultTableModel tableModel;
    private ModernTextField txtSearch;
    private ModernButton btnAdd, btnEdit, btnDelete, btnRefresh;

    public StudentPanel() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        loadStudents();
    }

    private void initComponents() {
        // Top panel với search và buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Title
        JLabel lblTitle = new JLabel("Quản lý Sinh viên");
        lblTitle.setFont(AppTheme.TITLE_FONT);
        lblTitle.setForeground(AppTheme.PRIMARY_COLOR);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("🔍 Tìm kiếm:");
        lblSearch.setFont(AppTheme.NORMAL_FONT);

        txtSearch = new ModernTextField(20);
        txtSearch.setPreferredSize(new Dimension(300, AppTheme.INPUT_HEIGHT));

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        btnAdd = new ModernButton("Thêm mới");
        btnAdd.setBackground(AppTheme.SUCCESS_COLOR);
        btnAdd.addActionListener(e -> showAddDialog());

        btnEdit = new ModernButton("Sửa");
        btnEdit.setBackground(AppTheme.WARNING_COLOR);
        btnEdit.addActionListener(e -> showEditDialog());
        btnEdit.setEnabled(false);

        btnDelete = new ModernButton("Xóa");
        btnDelete.setBackground(AppTheme.DANGER_COLOR);
        btnDelete.addActionListener(e -> deleteStudent());
        btnDelete.setEnabled(false);

        ModernButton btnResetPassword = new ModernButton("Cấp lại mật khẩu");
        btnResetPassword.setBackground(new Color(255, 165, 0)); // Orange color
        btnResetPassword.addActionListener(e -> resetPassword());
        btnResetPassword.setEnabled(false);

        btnRefresh = new ModernButton("Làm mới");
        btnRefresh.addActionListener(e -> loadStudents());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnResetPassword);
        buttonPanel.add(btnRefresh);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitle, BorderLayout.NORTH);
        titlePanel.add(searchPanel, BorderLayout.CENTER);

        topPanel.add(titlePanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Mã SV", "Họ tên", "Giới tính", "Ngày sinh", "Email", "Lớp"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setFont(AppTheme.NORMAL_FONT);
        studentTable.setRowHeight(35);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getTableHeader().setFont(AppTheme.NORMAL_FONT);
        studentTable.getTableHeader().setBackground(AppTheme.PRIMARY_COLOR);
        studentTable.getTableHeader().setForeground(Color.WHITE);
        studentTable.setSelectionBackground(new Color(41, 128, 185, 50));

        // Selection listener
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = studentTable.getSelectedRow() != -1;
            btnEdit.setEnabled(hasSelection);
            btnDelete.setEnabled(hasSelection);
            btnResetPassword.setEnabled(hasSelection);
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadStudents() {
        SwingWorker<List<StudentResponse>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<StudentResponse> doInBackground() throws Exception {
                String json = ApiClient.get("/students");
                return ApiClient.getObjectMapper().readValue(json, new TypeReference<List<StudentResponse>>() {});
            }

            @Override
            protected void done() {
                try {
                    List<StudentResponse> students = get();
                    tableModel.setRowCount(0);

                    for (StudentResponse student : students) {
                        tableModel.addRow(new Object[]{
                                student.getId(),
                                student.getStudentCode(),
                                student.getFullName(),
                                student.getGender(),
                                student.getDob(),
                                student.getEmail(),
                                student.getClassName() != null ? student.getClassName() : "Chưa có lớp"
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StudentPanel.this,
                            "Lỗi khi tải danh sách sinh viên: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void showAddDialog() {
        StudentDialog dialog = new StudentDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Thêm Sinh viên mới",
                null
        );
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            loadStudents();
        }
    }

    private void showEditDialog() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) return;

        Long studentId = (Long) tableModel.getValueAt(selectedRow, 0);

        // Load student details
        SwingWorker<StudentResponse, Void> worker = new SwingWorker<>() {
            @Override
            protected StudentResponse doInBackground() throws Exception {
                String json = ApiClient.get("/students/" + studentId);
                return ApiClient.getObjectMapper().readValue(json, StudentResponse.class);
            }

            @Override
            protected void done() {
                try {
                    StudentResponse student = get();
                    StudentDialog dialog = new StudentDialog(
                            (JFrame) SwingUtilities.getWindowAncestor(StudentPanel.this),
                            "Sửa thông tin Sinh viên",
                            student
                    );
                    dialog.setVisible(true);

                    if (dialog.isConfirmed()) {
                        loadStudents();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StudentPanel.this,
                            "Lỗi khi tải thông tin sinh viên: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) return;

        Long studentId = (Long) tableModel.getValueAt(selectedRow, 0);
        String studentName = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa sinh viên: " + studentName + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    ApiClient.delete("/students/" + studentId);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(StudentPanel.this,
                                "Xóa sinh viên thành công!",
                                "Thành công",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadStudents();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(StudentPanel.this,
                                "Lỗi khi xóa sinh viên: " + ex.getMessage(),
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    private void resetPassword() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) return;

        Long studentId = (Long) tableModel.getValueAt(selectedRow, 0);
        String studentName = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn cấp lại mật khẩu cho sinh viên: " + studentName + "?\nMật khẩu mới sẽ là: 12345678",
                "Xác nhận cấp lại mật khẩu",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    ApiClient.post("/students/" + studentId + "/reset-password", "");
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(StudentPanel.this,
                                "Cấp lại mật khẩu thành công!\nMật khẩu mới: 12345678",
                                "Thành công",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(StudentPanel.this,
                                "Lỗi khi cấp lại mật khẩu: " + ex.getMessage(),
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
}
