package iuh.fit.se.gui.view;

import com.fasterxml.jackson.core.type.TypeReference;
import iuh.fit.se.dto.response.LecturerResponse;
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
 * Panel quản lý Giảng viên
 */
public class LecturerPanel extends JPanel {

    private JTable lecturerTable;
    private DefaultTableModel tableModel;
    private ModernTextField txtSearch;
    private ModernButton btnAdd, btnEdit, btnDelete, btnRefresh;
    private ModernButton btnSearch;

    public LecturerPanel() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        loadLecturers();
    }

    private void initComponents() {
        // Top panel với search và buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Title
        JLabel lblTitle = new JLabel("👨‍🏫 Quản lý Giảng viên");
        lblTitle.setFont(AppTheme.TITLE_FONT);
        lblTitle.setForeground(AppTheme.PRIMARY_COLOR);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("🔍 Tìm kiếm:");
        lblSearch.setFont(AppTheme.NORMAL_FONT);

        txtSearch = new ModernTextField(20);
        txtSearch.setPreferredSize(new Dimension(300, AppTheme.INPUT_HEIGHT));

        // Search button
        btnSearch = new ModernButton("Tìm");
        btnSearch.setPreferredSize(new Dimension(100, 30));
        btnSearch.setBackground(AppTheme.PRIMARY_COLOR);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> performSearch());

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);// Search button


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
        btnDelete.addActionListener(e -> deleteLecturer());
        btnDelete.setEnabled(false);

        btnRefresh = new ModernButton("Làm mới");
        btnRefresh.addActionListener(e -> loadLecturers());

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
        String[] columns = {"STT", "ID", "Mã GV", "Họ tên", "Email", "Điện thoại", "Khoa", "Học vị", "Giới tính"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        lecturerTable = new JTable(tableModel);
        lecturerTable.setFont(AppTheme.NORMAL_FONT);
        lecturerTable.setRowHeight(35);
        lecturerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lecturerTable.getTableHeader().setFont(AppTheme.NORMAL_FONT);
        lecturerTable.getTableHeader().setBackground(AppTheme.PRIMARY_COLOR);
        lecturerTable.getTableHeader().setForeground(Color.BLACK);
        lecturerTable.setSelectionBackground(new Color(200, 18, 18, 255));

        // Selection listener
        lecturerTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = lecturerTable.getSelectedRow() != -1;
            btnEdit.setEnabled(hasSelection);
            btnDelete.setEnabled(hasSelection);
        });

        JScrollPane scrollPane = new JScrollPane(lecturerTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadLecturers() {
        txtSearch.setText("");
        SwingWorker<List<LecturerResponse>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<LecturerResponse> doInBackground() throws Exception {
                String json = ApiClient.get("/lecturers");
                return ApiClient.getObjectMapper().readValue(json, new TypeReference<List<LecturerResponse>>() {});
            }

            @Override
            protected void done() {
                try {
                    List<LecturerResponse> lecturers = get();
                    tableModel.setRowCount(0);

                    int stt = 1;
                    for (LecturerResponse lecturer : lecturers) {
                        tableModel.addRow(new Object[]{
                                stt++,
                                lecturer.getId(),
                                lecturer.getLecturerCode(),
                                lecturer.getFullName(),
                                lecturer.getEmail(),
                                lecturer.getPhone(),
                                lecturer.getFaculty(),
                                lecturer.getDegree(),
                                lecturer.getGender()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LecturerPanel.this,
                            "Lỗi khi tải danh sách giảng viên: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void showAddDialog() {
        LecturerDialog dialog = new LecturerDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Thêm Giảng viên mới",
                null
        );
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            loadLecturers();
        }
    }

    private void showEditDialog() {
        int selectedRow = lecturerTable.getSelectedRow();
        if (selectedRow == -1) return;

        Long lecturerId = (Long) tableModel.getValueAt(selectedRow, 1);

        // Load lecturer details
        SwingWorker<LecturerResponse, Void> worker = new SwingWorker<>() {
            @Override
            protected LecturerResponse doInBackground() throws Exception {
                String json = ApiClient.get("/lecturers/" + lecturerId);
                return ApiClient.getObjectMapper().readValue(json, LecturerResponse.class);
            }

            @Override
            protected void done() {
                try {
                    LecturerResponse lecturer = get();
                    LecturerDialog dialog = new LecturerDialog(
                            (JFrame) SwingUtilities.getWindowAncestor(LecturerPanel.this),
                            "Sửa thông tin Giảng viên",
                            lecturer
                    );
                    dialog.setVisible(true);

                    if (dialog.isConfirmed()) {
                        loadLecturers();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LecturerPanel.this,
                            "Lỗi khi tải thông tin giảng viên: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void deleteLecturer() {
        int selectedRow = lecturerTable.getSelectedRow();
        if (selectedRow == -1) return;

        Long lecturerId = (Long) tableModel.getValueAt(selectedRow, 1);
        String lecturerName = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa giảng viên: " + lecturerName + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    ApiClient.delete("/lecturers/" + lecturerId);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(LecturerPanel.this,
                                "Xóa giảng viên thành công!",
                                "Thành công",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadLecturers();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(LecturerPanel.this,
                                "Lỗi khi xóa giảng viên: " + ex.getMessage(),
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    private void performSearch() {
        String query = txtSearch.getText();
        if (query == null || query.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập tên hoặc mã giảng viên để tìm.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String q = query.trim().toLowerCase();

        int foundModelRow = -1;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String code = safeString(tableModel.getValueAt(i, 1)).toLowerCase();
            String name = safeString(tableModel.getValueAt(i, 2)).toLowerCase();
            if (code.contains(q) || name.contains(q)) {
                foundModelRow = i;
                break;
            }
        }

        if (foundModelRow == -1) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy giảng viên phù hợp.", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
            loadLecturers();
            return;
        }

        // convert model row to view row in case table is sorted/filtered later
        int viewRow = lecturerTable.convertRowIndexToView(foundModelRow);
        lecturerTable.setRowSelectionInterval(viewRow, viewRow);
        Rectangle rect = lecturerTable.getCellRect(viewRow, 0, true);
        lecturerTable.scrollRectToVisible(rect);
    }

    private String safeString(Object o) {
        return o != null ? o.toString() : "";
    }
}
