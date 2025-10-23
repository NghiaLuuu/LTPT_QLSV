package iuh.fit.se.client.gui.panels;

import iuh.fit.se.client.gui.AdminMainFrame;
import iuh.fit.se.client.net.NetworkClient;
import iuh.fit.se.common.dto.HocKyDTO;
import iuh.fit.se.common.protocol.Command;
import iuh.fit.se.common.protocol.Request;
import iuh.fit.se.common.protocol.Response;
import iuh.fit.se.common.protocol.Status;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HocKyPanel extends JPanel implements AdminMainFrame.RefreshablePanel {
    private NetworkClient networkClient;
    private String authToken;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaHocKy, txtTenHocKy, txtNgayBatDau, txtNgayKetThuc;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnClear;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public HocKyPanel(NetworkClient networkClient, String authToken) {
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

        String[] columnNames = {"Mã Học kỳ", "Tên Học kỳ", "Ngày bắt đầu", "Ngày kết thúc"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);

        txtMaHocKy = new JTextField(15);
        txtTenHocKy = new JTextField(30);
        txtNgayBatDau = new JTextField(12);
        txtNgayKetThuc = new JTextField(12);

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
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Học kỳ"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Mã Học kỳ:"), gbc);
        gbc.gridx = 1;
        topPanel.add(txtMaHocKy, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Tên Học kỳ:"), gbc);
        gbc.gridx = 3;
        topPanel.add(txtTenHocKy, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Ngày bắt đầu (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        topPanel.add(txtNgayBatDau, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Ngày kết thúc (dd/MM/yyyy):"), gbc);
        gbc.gridx = 3;
        topPanel.add(txtNgayKetThuc, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnRefresh);
        topPanel.add(buttonPanel, gbc);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                txtMaHocKy.setText(tableModel.getValueAt(row, 0).toString());
                txtTenHocKy.setText(tableModel.getValueAt(row, 1).toString());
                txtNgayBatDau.setText(tableModel.getValueAt(row, 2).toString());
                txtNgayKetThuc.setText(tableModel.getValueAt(row, 3).toString());
                txtMaHocKy.setEditable(false);
            }
        });

        btnAdd.addActionListener(e -> addHocKy());
        btnUpdate.addActionListener(e -> updateHocKy());
        btnDelete.addActionListener(e -> deleteHocKy());
        btnRefresh.addActionListener(e -> refresh());
        btnClear.addActionListener(e -> clearForm());
    }

    private void addHocKy() {
        try {
            String maHocKy = txtMaHocKy.getText().trim();
            String tenHocKy = txtTenHocKy.getText().trim();
            LocalDate ngayBatDau = LocalDate.parse(txtNgayBatDau.getText().trim(), formatter);
            LocalDate ngayKetThuc = LocalDate.parse(txtNgayKetThuc.getText().trim(), formatter);

            if (maHocKy.isEmpty() || tenHocKy.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            HocKyDTO dto = new HocKyDTO(maHocKy, tenHocKy, ngayBatDau, ngayKetThuc);
            Response response = networkClient.sendRequest(new Request(Command.HOCKY_ADD, dto, authToken));

            if (response.getStatus() == Status.SUCCESS) {
                JOptionPane.showMessageDialog(this, "Thêm học kỳ thành công!");
                clearForm();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage() + "\nĐịnh dạng ngày: dd/MM/yyyy",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateHocKy() {
        try {
            String maHocKy = txtMaHocKy.getText().trim();
            if (maHocKy.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn học kỳ!");
                return;
            }

            LocalDate ngayBatDau = LocalDate.parse(txtNgayBatDau.getText().trim(), formatter);
            LocalDate ngayKetThuc = LocalDate.parse(txtNgayKetThuc.getText().trim(), formatter);

            HocKyDTO dto = new HocKyDTO(maHocKy, txtTenHocKy.getText().trim(), ngayBatDau, ngayKetThuc);
            Response response = networkClient.sendRequest(new Request(Command.HOCKY_UPDATE, dto, authToken));

            if (response.getStatus() == Status.SUCCESS) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                clearForm();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteHocKy() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học kỳ!");
            return;
        }

        String maHocKy = tableModel.getValueAt(row, 0).toString();
        if (JOptionPane.showConfirmDialog(this, "Xóa học kỳ " + maHocKy + "?", "Xác nhận",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                Response response = networkClient.sendRequest(new Request(Command.HOCKY_DELETE, maHocKy, authToken));
                if (response.getStatus() == Status.SUCCESS) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    clearForm();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void refresh() {
        loadData();
    }

    private void loadData() {
        try {
            Response response = networkClient.sendRequest(new Request(Command.HOCKY_GET_ALL, null, authToken));
            if (response.getStatus() == Status.SUCCESS && response.getData() instanceof List) {
                @SuppressWarnings("unchecked")
                List<HocKyDTO> list = (List<HocKyDTO>) response.getData();
                tableModel.setRowCount(0);
                for (HocKyDTO dto : list) {
                    tableModel.addRow(new Object[]{
                        dto.getMaHocKy(),
                        dto.getTenHocKy(),
                        dto.getNgayBatDau().format(formatter),
                        dto.getNgayKetThuc().format(formatter)
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }

    private void clearForm() {
        txtMaHocKy.setText("");
        txtTenHocKy.setText("");
        txtNgayBatDau.setText("");
        txtNgayKetThuc.setText("");
        txtMaHocKy.setEditable(true);
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

