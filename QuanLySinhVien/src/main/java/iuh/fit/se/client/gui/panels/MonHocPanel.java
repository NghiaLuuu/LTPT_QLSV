package iuh.fit.se.client.gui.panels;

import iuh.fit.se.client.gui.AdminMainFrame;
import iuh.fit.se.client.net.NetworkClient;
import iuh.fit.se.common.dto.KhoaDTO;
import iuh.fit.se.common.dto.MonHocDTO;
import iuh.fit.se.common.protocol.Command;
import iuh.fit.se.common.protocol.Request;
import iuh.fit.se.common.protocol.Response;
import iuh.fit.se.common.protocol.Status;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MonHocPanel extends JPanel implements AdminMainFrame.RefreshablePanel {
    private NetworkClient networkClient;
    private String authToken;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaMH, txtTenMH, txtSoTinChi;
    private JComboBox<String> cboKhoa;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnClear;

    public MonHocPanel(NetworkClient networkClient, String authToken) {
        this.networkClient = networkClient;
        this.authToken = authToken;
        initComponents();
        setupLayout();
        setupEventHandlers();
        loadKhoa();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"Mã MH", "Tên môn học", "Số tín chỉ", "Mã Khoa", "Tên Khoa"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);

        txtMaMH = new JTextField(15);
        txtTenMH = new JTextField(30);
        txtSoTinChi = new JTextField(10);
        cboKhoa = new JComboBox<>();

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
        topPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Môn học"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Mã MH:"), gbc);
        gbc.gridx = 1;
        topPanel.add(txtMaMH, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Tên môn học:"), gbc);
        gbc.gridx = 3;
        topPanel.add(txtTenMH, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Số tín chỉ:"), gbc);
        gbc.gridx = 1;
        topPanel.add(txtSoTinChi, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Khoa:"), gbc);
        gbc.gridx = 3;
        topPanel.add(cboKhoa, gbc);

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
                txtMaMH.setText(tableModel.getValueAt(row, 0).toString());
                txtTenMH.setText(tableModel.getValueAt(row, 1).toString());
                txtSoTinChi.setText(tableModel.getValueAt(row, 2).toString());
                cboKhoa.setSelectedItem(tableModel.getValueAt(row, 3).toString());
                txtMaMH.setEditable(false);
            }
        });

        btnAdd.addActionListener(e -> addMonHoc());
        btnUpdate.addActionListener(e -> updateMonHoc());
        btnDelete.addActionListener(e -> deleteMonHoc());
        btnRefresh.addActionListener(e -> refresh());
        btnClear.addActionListener(e -> clearForm());
    }

    private void loadKhoa() {
        try {
            Response response = networkClient.sendRequest(new Request(Command.KHOA_GET_ALL, null, authToken));
            if (response.getStatus() == Status.SUCCESS && response.getData() instanceof List) {
                @SuppressWarnings("unchecked")
                List<KhoaDTO> list = (List<KhoaDTO>) response.getData();
                cboKhoa.removeAllItems();
                for (KhoaDTO dto : list) cboKhoa.addItem(dto.getMaKhoa());
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void addMonHoc() {
        String maMH = txtMaMH.getText().trim();
        String tenMH = txtTenMH.getText().trim();
        String soTinChiStr = txtSoTinChi.getText().trim();
        String maKhoa = (String) cboKhoa.getSelectedItem();

        if (maMH.isEmpty() || tenMH.isEmpty() || soTinChiStr.isEmpty() || maKhoa == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int soTinChi = Integer.parseInt(soTinChiStr);
            MonHocDTO dto = new MonHocDTO(maMH, tenMH, soTinChi, maKhoa, null);
            Response response = networkClient.sendRequest(new Request(Command.MONHOC_ADD, dto, authToken));
            if (response.getStatus() == Status.SUCCESS) {
                JOptionPane.showMessageDialog(this, "Thêm môn học thành công!");
                clearForm();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tín chỉ phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMonHoc() {
        String maMH = txtMaMH.getText().trim();
        if (maMH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn môn học!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int soTinChi = Integer.parseInt(txtSoTinChi.getText().trim());
            MonHocDTO dto = new MonHocDTO(maMH, txtTenMH.getText().trim(), soTinChi,
                                         (String) cboKhoa.getSelectedItem(), null);
            Response response = networkClient.sendRequest(new Request(Command.MONHOC_UPDATE, dto, authToken));
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

    private void deleteMonHoc() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn môn học!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maMH = tableModel.getValueAt(row, 0).toString();
        if (JOptionPane.showConfirmDialog(this, "Xóa môn học " + maMH + "?", "Xác nhận",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                Response response = networkClient.sendRequest(new Request(Command.MONHOC_DELETE, maMH, authToken));
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
        loadKhoa();
    }

    private void loadData() {
        try {
            Response response = networkClient.sendRequest(new Request(Command.MONHOC_GET_ALL, null, authToken));
            if (response.getStatus() == Status.SUCCESS && response.getData() instanceof List) {
                @SuppressWarnings("unchecked")
                List<MonHocDTO> list = (List<MonHocDTO>) response.getData();
                tableModel.setRowCount(0);
                for (MonHocDTO dto : list) {
                    tableModel.addRow(new Object[]{dto.getMaMH(), dto.getTenMH(), dto.getSoTinChi(),
                                                   dto.getMaKhoa(), dto.getTenKhoa()});
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }

    private void clearForm() {
        txtMaMH.setText("");
        txtTenMH.setText("");
        txtSoTinChi.setText("");
        if (cboKhoa.getItemCount() > 0) cboKhoa.setSelectedIndex(0);
        txtMaMH.setEditable(true);
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

