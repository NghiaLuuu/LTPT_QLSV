package iuh.fit.se.client.gui.panels;

import iuh.fit.se.client.gui.AdminMainFrame;
import iuh.fit.se.client.net.NetworkClient;
import iuh.fit.se.common.dto.KhoaDTO;
import iuh.fit.se.common.protocol.Command;
import iuh.fit.se.common.protocol.Request;
import iuh.fit.se.common.protocol.Response;
import iuh.fit.se.common.protocol.Status;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

    public KhoaPanel(NetworkClient networkClient, String authToken) {
        this.networkClient = networkClient;
        this.authToken = authToken;

        initComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        String[] columnNames = {"Mã Khoa", "Tên Khoa"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setFont(new Font("Arial", Font.PLAIN, 12));

        // Input fields
        txtMaKhoa = new JTextField(20);
        txtTenKhoa = new JTextField(30);

        // Buttons
        btnAdd = new JButton("➕ Thêm");
        btnUpdate = new JButton("✏️ Cập nhật");
        btnDelete = new JButton("🗑️ Xóa");
        btnRefresh = new JButton("🔄 Làm mới");
        btnClear = new JButton("🧹 Xóa trắng");

        styleButton(btnAdd, new Color(40, 167, 69));
        styleButton(btnUpdate, new Color(0, 123, 255));
        styleButton(btnDelete, new Color(220, 53, 69));
        styleButton(btnRefresh, new Color(108, 117, 125));
        styleButton(btnClear, new Color(255, 193, 7));
    }

    private void setupLayout() {
        // Top panel - Form nhập liệu
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Khoa"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mã Khoa
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Mã Khoa:"), gbc);
        gbc.gridx = 1;
        topPanel.add(txtMaKhoa, gbc);

        // Tên Khoa
        gbc.gridx = 2;
        topPanel.add(new JLabel("Tên Khoa:"), gbc);
        gbc.gridx = 3;
        topPanel.add(txtTenKhoa, gbc);

        // Buttons
        gbc.gridx = 4; gbc.gridy = 0;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnRefresh);
        topPanel.add(buttonPanel, gbc);

        add(topPanel, BorderLayout.NORTH);

        // Center panel - Table
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách Khoa"));
        add(scrollPane, BorderLayout.CENTER);
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
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập đầy đủ thông tin!",
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        KhoaDTO dto = new KhoaDTO(maKhoa, tenKhoa);
        Request request = new Request(Command.KHOA_ADD, dto, authToken);

        try {
            Response response = networkClient.sendRequest(request);
            if (response.getStatus() == Status.SUCCESS) {
                JOptionPane.showMessageDialog(this,
                    "Thêm khoa thành công!",
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

    private void updateKhoa() {
        String maKhoa = txtMaKhoa.getText().trim();
        String tenKhoa = txtTenKhoa.getText().trim();

        if (maKhoa.isEmpty() || tenKhoa.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn khoa cần cập nhật!",
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        KhoaDTO dto = new KhoaDTO(maKhoa, tenKhoa);
        Request request = new Request(Command.KHOA_UPDATE, dto, authToken);

        try {
            Response response = networkClient.sendRequest(request);
            if (response.getStatus() == Status.SUCCESS) {
                JOptionPane.showMessageDialog(this,
                    "Cập nhật khoa thành công!",
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
}

