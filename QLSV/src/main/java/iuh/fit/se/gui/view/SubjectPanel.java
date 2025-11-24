package iuh.fit.se.gui.view;

import com.fasterxml.jackson.core.type.TypeReference;
import iuh.fit.se.gui.component.ModernButton;
import iuh.fit.se.gui.component.ModernTextField;
import iuh.fit.se.gui.util.ApiClient;
import iuh.fit.se.gui.util.AppTheme;
import iuh.fit.se.model.Subject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel quản lý Môn học
 */
public class SubjectPanel extends JPanel {

    private JTable subjectTable;
    private DefaultTableModel tableModel;
    private ModernTextField txtSearch;
    private ModernButton btnAdd, btnEdit, btnDelete, btnRefresh;
    private ModernButton btnSearch;

    public SubjectPanel() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        loadSubjects();
    }

    private void initComponents() {
        // Top panel với search và buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Title
        JLabel lblTitle = new JLabel("📚 Quản lý Môn học");
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
        searchPanel.add(btnSearch);

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
        btnDelete.addActionListener(e -> deleteSubject());
        btnDelete.setEnabled(false);

        btnRefresh = new ModernButton("Làm mới");
        btnRefresh.addActionListener(e -> loadSubjects());

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
        String[] columns = {"STT", "ID","Mã môn học", "Tên môn học", "Số tín chỉ"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        subjectTable = new JTable(tableModel);
        subjectTable.setFont(AppTheme.NORMAL_FONT);
        subjectTable.setRowHeight(35);
        subjectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        subjectTable.getTableHeader().setFont(AppTheme.NORMAL_FONT);
        subjectTable.getTableHeader().setBackground(AppTheme.PRIMARY_COLOR);
        subjectTable.getTableHeader().setForeground(Color.BLACK);
        subjectTable.setSelectionBackground(new Color(200, 18, 18, 255));

        // Selection listener
        subjectTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = subjectTable.getSelectedRow() != -1;
            btnEdit.setEnabled(hasSelection);
            btnDelete.setEnabled(hasSelection);
        });

        JScrollPane scrollPane = new JScrollPane(subjectTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void performSearch() {
        String query = txtSearch.getText();
        if (query == null || query.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập tên hoặc mã sinh viên để tìm.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Không tìm thấy sinh viên phù hợp.", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
            loadSubjects();
            return;
        }

        // convert model row to view row in case table is sorted/filtered later
        int viewRow = subjectTable.convertRowIndexToView(foundModelRow);
        subjectTable.setRowSelectionInterval(viewRow, viewRow);
        Rectangle rect = subjectTable.getCellRect(viewRow, 0, true);
        subjectTable.scrollRectToVisible(rect);
    }

    private void loadSubjects() {
        txtSearch.setText("");
        SwingWorker<List<Subject>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Subject> doInBackground() throws Exception {
                String json = ApiClient.get("/subjects");
                return ApiClient.getObjectMapper().readValue(json, new TypeReference<List<Subject>>() {});
            }

            @Override
            protected void done() {
                try {
                    List<Subject> subjects = get();
                    tableModel.setRowCount(0);

                    int stt = 1;
                    for (Subject subject : subjects) {
                        tableModel.addRow(new Object[]{
                                stt++,
                                subject.getId(),
                                subject.getCode(),
                                subject.getName(),
                                subject.getCredit()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(SubjectPanel.this,
                            "Lỗi khi tải danh sách môn học: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void showAddDialog() {
        SubjectDialog dialog = new SubjectDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm môn học", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadSubjects();
        }
    }

    private void showEditDialog() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        Long subjectId = (Long) tableModel.getValueAt(selectedRow, 1);

        // Load full subject data
        SwingWorker<Subject, Void> worker = new SwingWorker<>() {
            @Override
            protected Subject doInBackground() throws Exception {
                String json = ApiClient.get("/subjects/" + subjectId);
                return ApiClient.getObjectMapper().readValue(json, Subject.class);
            }

            @Override
            protected void done() {
                try {
                    Subject subject = get();
                    SubjectDialog dialog = new SubjectDialog(
                            (JFrame) SwingUtilities.getWindowAncestor(SubjectPanel.this),
                            "Sửa thông tin môn học",
                            subject
                    );
                    dialog.setVisible(true);
                    if (dialog.isConfirmed()) {
                        loadSubjects();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(SubjectPanel.this,
                            "Lỗi khi tải thông tin môn học: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void deleteSubject() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        Long subjectId = (Long) tableModel.getValueAt(selectedRow, 1);
        String subjectName = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa môn học: " + subjectName + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    ApiClient.delete("/subjects/" + subjectId);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(SubjectPanel.this,
                                "Xóa môn học thành công!",
                                "Thành công",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadSubjects();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(SubjectPanel.this,
                                "Lỗi khi xóa môn học: " + ex.getMessage(),
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
    private String safeString(Object o) {
        return o != null ? o.toString() : "";
    }
}
