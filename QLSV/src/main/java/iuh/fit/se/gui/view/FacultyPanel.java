package iuh.fit.se.gui.view;

import com.fasterxml.jackson.core.type.TypeReference;
import iuh.fit.se.dto.response.FacultyResponse;
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
 * Panel quản lý Khoa
 */
public class FacultyPanel extends JPanel {

    private JTable facultyTable;
    private DefaultTableModel tableModel;
    private ModernTextField txtSearch;
    private ModernButton btnAdd, btnEdit, btnDelete, btnRefresh;

    public FacultyPanel() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        loadFaculties();
    }

    private void initComponents() {
        // Top panel với search và buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Title
        JLabel lblTitle = new JLabel("Quản lý Khoa");
        lblTitle.setFont(AppTheme.TITLE_FONT);
        lblTitle.setForeground(AppTheme.PRIMARY_COLOR);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("Tìm kiếm:");
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
        btnDelete.addActionListener(e -> deleteFaculty());
        btnDelete.setEnabled(false);

        btnRefresh = new ModernButton("Làm mới");
        btnRefresh.addActionListener(e -> loadFaculties());

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
        String[] columns = {"ID", "Mã Khoa", "Tên Khoa", "Mô tả", "Số lớp"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        facultyTable = new JTable(tableModel);
        facultyTable.setFont(AppTheme.NORMAL_FONT);
        facultyTable.setRowHeight(35);
        facultyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        facultyTable.getTableHeader().setFont(AppTheme.NORMAL_FONT);
        facultyTable.getTableHeader().setBackground(AppTheme.PRIMARY_COLOR);
        facultyTable.getTableHeader().setForeground(Color.WHITE);
        facultyTable.setSelectionBackground(new Color(41, 128, 185, 50));

        // Selection listener
        facultyTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = facultyTable.getSelectedRow() != -1;
            btnEdit.setEnabled(hasSelection);
            btnDelete.setEnabled(hasSelection);
        });

        JScrollPane scrollPane = new JScrollPane(facultyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadFaculties() {
        SwingWorker<List<FacultyResponse>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<FacultyResponse> doInBackground() throws Exception {
                String json = ApiClient.get("/faculties");
                return ApiClient.getObjectMapper().readValue(json, new TypeReference<List<FacultyResponse>>() {});
            }

            @Override
            protected void done() {
                try {
                    List<FacultyResponse> faculties = get();
                    tableModel.setRowCount(0);

                    for (FacultyResponse faculty : faculties) {
                        tableModel.addRow(new Object[]{
                                faculty.getId(),
                                faculty.getCode(),
                                faculty.getName(),
                                faculty.getDescription(),
                                faculty.getClassCount()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FacultyPanel.this,
                            "Lỗi khi tải danh sách khoa: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void showAddDialog() {
        FacultyDialog dialog = new FacultyDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Thêm Khoa mới",
                null
        );
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            loadFaculties();
        }
    }

    private void showEditDialog() {
        int selectedRow = facultyTable.getSelectedRow();
        if (selectedRow == -1) return;

        Long facultyId = (Long) tableModel.getValueAt(selectedRow, 0);

        // Load faculty details
        SwingWorker<FacultyResponse, Void> worker = new SwingWorker<>() {
            @Override
            protected FacultyResponse doInBackground() throws Exception {
                String json = ApiClient.get("/faculties/" + facultyId);
                return ApiClient.getObjectMapper().readValue(json, FacultyResponse.class);
            }

            @Override
            protected void done() {
                try {
                    FacultyResponse faculty = get();
                    FacultyDialog dialog = new FacultyDialog(
                            (JFrame) SwingUtilities.getWindowAncestor(FacultyPanel.this),
                            "Sửa thông tin Khoa",
                            faculty
                    );
                    dialog.setVisible(true);

                    if (dialog.isConfirmed()) {
                        loadFaculties();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FacultyPanel.this,
                            "Lỗi khi tải thông tin khoa: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void deleteFaculty() {
        int selectedRow = facultyTable.getSelectedRow();
        if (selectedRow == -1) return;

        Long facultyId = (Long) tableModel.getValueAt(selectedRow, 0);
        String facultyName = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa khoa: " + facultyName + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    ApiClient.delete("/faculties/" + facultyId);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(FacultyPanel.this,
                                "Xóa khoa thành công!",
                                "Thành công",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadFaculties();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(FacultyPanel.this,
                                "Lỗi khi xóa khoa: " + ex.getMessage(),
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
}

