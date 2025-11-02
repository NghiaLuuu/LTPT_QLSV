package iuh.fit.se.gui.view;

import com.fasterxml.jackson.core.type.TypeReference;
import iuh.fit.se.gui.component.ModernButton;
import iuh.fit.se.gui.component.ModernTextField;
import iuh.fit.se.gui.util.ApiClient;
import iuh.fit.se.gui.util.AppTheme;
import iuh.fit.se.model.Class;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel quản lý Lớp học
 */
public class ClassPanel extends JPanel {

    private JTable classTable;
    private DefaultTableModel tableModel;
    private ModernTextField txtSearch;
    private ModernButton btnAdd, btnEdit, btnDelete, btnRefresh;

    public ClassPanel() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        loadClasses();
    }

    private void initComponents() {
        // Top panel với search và buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Title
        JLabel lblTitle = new JLabel("🏫 Quản lý Lớp học");
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
        btnDelete.addActionListener(e -> deleteClass());
        btnDelete.setEnabled(false);

        btnRefresh = new ModernButton("Làm mới");
        btnRefresh.addActionListener(e -> loadClasses());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitle, BorderLayout.NORTH);
        titlePanel.add(searchPanel, BorderLayout.CENTER);

        topPanel.add(titlePanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Tên lớp", "Khoa", "Khóa học", "Số sinh viên"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        classTable = new JTable(tableModel);
        classTable.setFont(AppTheme.NORMAL_FONT);
        classTable.setRowHeight(35);
        classTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classTable.getTableHeader().setFont(AppTheme.NORMAL_FONT);
        classTable.getTableHeader().setBackground(AppTheme.PRIMARY_COLOR);
        classTable.getTableHeader().setForeground(Color.WHITE);
        classTable.setSelectionBackground(new Color(41, 128, 185, 50));

        // Selection listener
        classTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = classTable.getSelectedRow() != -1;
            btnEdit.setEnabled(hasSelection);
            btnDelete.setEnabled(hasSelection);
        });

        JScrollPane scrollPane = new JScrollPane(classTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadClasses() {
        SwingWorker<List<Class>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Class> doInBackground() throws Exception {
                String json = ApiClient.get("/classes");
                return ApiClient.getObjectMapper().readValue(json, new TypeReference<List<Class>>() {});
            }

            @Override
            protected void done() {
                try {
                    List<Class> classes = get();
                    tableModel.setRowCount(0);

                    for (Class cls : classes) {
                        int studentCount = cls.getStudents() != null ? cls.getStudents().size() : 0;
                        String facultyName = cls.getFaculty() != null ? cls.getFaculty().getName() : "Chưa có khoa";
                        tableModel.addRow(new Object[]{
                                cls.getId(),
                                cls.getName(),
                                facultyName,
                                cls.getCourseYear(),
                                studentCount
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace(); // Print stack trace for debugging
                    String errorMessage = "Lỗi khi tải danh sách lớp học";

                    // Get more detailed error message
                    if (ex.getCause() != null) {
                        errorMessage += ":\n" + ex.getCause().getMessage();
                    } else {
                        errorMessage += ":\n" + ex.getMessage();
                    }

                    JOptionPane.showMessageDialog(ClassPanel.this,
                            errorMessage,
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void showAddDialog() {
        ClassDialog dialog = new ClassDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Thêm Lớp học mới",
                null
        );
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            loadClasses();
        }
    }

    private void showEditDialog() {
        int selectedRow = classTable.getSelectedRow();
        if (selectedRow == -1) return;

        Long classId = (Long) tableModel.getValueAt(selectedRow, 0);

        // Load class details
        SwingWorker<Class, Void> worker = new SwingWorker<>() {
            @Override
            protected Class doInBackground() throws Exception {
                String json = ApiClient.get("/classes/" + classId);
                return ApiClient.getObjectMapper().readValue(json, Class.class);
            }

            @Override
            protected void done() {
                try {
                    Class cls = get();
                    ClassDialog dialog = new ClassDialog(
                            (JFrame) SwingUtilities.getWindowAncestor(ClassPanel.this),
                            "Sửa thông tin Lớp học",
                            cls
                    );
                    dialog.setVisible(true);

                    if (dialog.isConfirmed()) {
                        loadClasses();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ClassPanel.this,
                            "Lỗi khi tải thông tin lớp học: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void deleteClass() {
        int selectedRow = classTable.getSelectedRow();
        if (selectedRow == -1) return;

        Long classId = (Long) tableModel.getValueAt(selectedRow, 0);
        String className = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa lớp học: " + className + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    ApiClient.delete("/classes/" + classId);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(ClassPanel.this,
                                "Xóa lớp học thành công!",
                                "Thành công",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadClasses();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ClassPanel.this,
                                "Lỗi khi xóa lớp học: " + ex.getMessage(),
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
}
