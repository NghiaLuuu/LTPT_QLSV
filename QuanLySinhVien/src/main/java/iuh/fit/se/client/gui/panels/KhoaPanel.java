package iuh.fit.se.client.gui.panels;

import iuh.fit.se.client.gui.AdminMainFrame;
import iuh.fit.se.client.net.NetworkClient;
import iuh.fit.se.common.dto.KhoaDTO;
import iuh.fit.se.common.protocol.Command;
import iuh.fit.se.common.protocol.Request;
import iuh.fit.se.common.protocol.Response;
import iuh.fit.se.common.protocol.Status;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * Panel quản lý Khoa (Faculty/Department)
 */
public class KhoaPanel extends JPanel implements AdminMainFrame.RefreshablePanel {
    private NetworkClient networkClient;
    private String authToken;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaKhoa;
    private JTextField txtTenKhoa;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnRefresh;
    private JButton btnClear;

    // Modern colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color SECONDARY_COLOR = new Color(149, 165, 166);
    private static final Color HEADER_BG = new Color(52, 73, 94);
    private static final Color TABLE_HEADER_BG = new Color(52, 152, 219);
    private static final Color TABLE_ROW_EVEN = new Color(236, 240, 241);
    private static final Color TABLE_ROW_ODD = Color.WHITE;

    public KhoaPanel(NetworkClient networkClient, String authToken) {
        this.networkClient = networkClient;
        this.authToken = authToken;

        setBackground(Color.WHITE);
        initComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Table with modern styling
        String[] columnNames = {"Mã Khoa", "Tên Khoa"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(189, 195, 199));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        // Modern table header
        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? TABLE_ROW_EVEN : TABLE_ROW_ODD);
                }
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });

        // Modern input fields
        txtMaKhoa = createModernTextField(20);
        txtTenKhoa = createModernTextField(30);

        // Modern buttons
        btnAdd = createModernButton("➕ Thêm", SUCCESS_COLOR);
        btnUpdate = createModernButton("✏️ Cập nhật", PRIMARY_COLOR);
        btnDelete = createModernButton("🗑️ Xóa", DANGER_COLOR);
        btnRefresh = createModernButton("🔄 Làm mới", SECONDARY_COLOR);
        btnClear = createModernButton("🧹 Xóa trắng", WARNING_COLOR);
    }

    private JTextField createModernTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return textField;
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 38));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void setupLayout() {
        // Top panel - Form nhập liệu với modern design
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Title
        JLabel titleLabel = new JLabel("📚 Thông tin Khoa");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(HEADER_BG);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(titleLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mã Khoa
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel lblMaKhoa = new JLabel("Mã Khoa:");
        lblMaKhoa.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(lblMaKhoa, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.3;
        formPanel.add(txtMaKhoa, gbc);

        // Spacing
        gbc.gridx = 2;
        gbc.weightx = 0;
        formPanel.add(Box.createRigidArea(new Dimension(30, 0)), gbc);

        // Tên Khoa
        gbc.gridx = 3;
        gbc.weightx = 0;
        JLabel lblTenKhoa = new JLabel("Tên Khoa:");
        lblTenKhoa.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(lblTenKhoa, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0.7;
        formPanel.add(txtTenKhoa, gbc);

        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(formPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnRefresh);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(buttonPanel);

        add(topPanel, BorderLayout.NORTH);

        // Center panel - Table với modern design
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel tableTitle = new JLabel("📋 Danh sách Khoa");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(HEADER_BG);
        centerPanel.add(tableTitle, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        // Table selection
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    txtMaKhoa.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    txtTenKhoa.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    txtMaKhoa.setEditable(false);
                }
            }
        });

        // Buttons
        btnAdd.addActionListener(e -> addKhoa());
        btnUpdate.addActionListener(e -> updateKhoa());
        btnDelete.addActionListener(e -> deleteKhoa());
        btnRefresh.addActionListener(e -> refresh());
        btnClear.addActionListener(e -> clearForm());
    }

    private void addKhoa() {
        String maKhoa = txtMaKhoa.getText().trim();
        String tenKhoa = txtTenKhoa.getText().trim();

        if (maKhoa.isEmpty() || tenKhoa.isEmpty()) {
            showModernDialog("Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        KhoaDTO dto = new KhoaDTO(maKhoa, tenKhoa);
        Request request = new Request(Command.KHOA_ADD, dto, authToken);

        try {
            Response response = networkClient.sendRequest(request);
            if (response.getStatus() == Status.SUCCESS) {
                showModernDialog("Thêm khoa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadData();
            } else {
                showModernDialog("Lỗi: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            showModernDialog("Lỗi kết nối: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateKhoa() {
        String maKhoa = txtMaKhoa.getText().trim();
        String tenKhoa = txtTenKhoa.getText().trim();

        if (maKhoa.isEmpty() || tenKhoa.isEmpty()) {
            showModernDialog("Vui lòng chọn khoa cần cập nhật!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        KhoaDTO dto = new KhoaDTO(maKhoa, tenKhoa);
        Request request = new Request(Command.KHOA_UPDATE, dto, authToken);

        try {
            Response response = networkClient.sendRequest(request);
            if (response.getStatus() == Status.SUCCESS) {
                showModernDialog("Cập nhật khoa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadData();
            } else {
                showModernDialog("Lỗi: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            showModernDialog("Lỗi kết nối: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteKhoa() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn khoa cần xóa!",
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maKhoa = tableModel.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa khoa " + maKhoa + "?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Request request = new Request(Command.KHOA_DELETE, maKhoa, authToken);

            try {
                Response response = networkClient.sendRequest(request);
                if (response.getStatus() == Status.SUCCESS) {
                    JOptionPane.showMessageDialog(this,
                        "Xóa khoa thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Lỗi: " + response.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Lỗi kết nối: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void refresh() {
        loadData();
    }

    private void loadData() {
        Request request = new Request(Command.KHOA_GET_ALL, null, authToken);

        try {
            Response response = networkClient.sendRequest(request);
            if (response.getStatus() == Status.SUCCESS && response.getData() instanceof List) {
                @SuppressWarnings("unchecked")
                List<KhoaDTO> list = (List<KhoaDTO>) response.getData();

                tableModel.setRowCount(0);
                for (KhoaDTO dto : list) {
                    tableModel.addRow(new Object[]{dto.getMaKhoa(), dto.getTenKhoa()});
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Lỗi tải dữ liệu: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtMaKhoa.setText("");
        txtTenKhoa.setText("");
        txtMaKhoa.setEditable(true);
        table.clearSelection();
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(120, 35));
    }

    private void showModernDialog(String message, String title, int messageType) {
        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel(message);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okButton.setBackground(PRIMARY_COLOR);
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.setBorderPainted(false);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.setPreferredSize(new Dimension(100, 40));
        okButton.addActionListener(e -> dialog.dispose());
        panel.add(okButton, BorderLayout.SOUTH);

        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}

