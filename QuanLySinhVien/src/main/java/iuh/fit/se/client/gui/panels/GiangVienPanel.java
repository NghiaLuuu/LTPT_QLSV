package iuh.fit.se.client.gui.panels;

import iuh.fit.se.client.gui.AdminMainFrame;
import iuh.fit.se.client.net.NetworkClient;
import iuh.fit.se.common.dto.GiangVienDTO;
import iuh.fit.se.common.dto.KhoaDTO;
import iuh.fit.se.common.protocol.Command;
import iuh.fit.se.common.protocol.Request;
import iuh.fit.se.common.protocol.Response;
import iuh.fit.se.common.protocol.Status;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GiangVienPanel extends JPanel implements AdminMainFrame.RefreshablePanel {
    private NetworkClient networkClient;
    private String authToken;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaGV, txtHoTen, txtHocVi;
    private JComboBox<String> cboKhoa;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnClear;

    public GiangVienPanel(NetworkClient networkClient, String authToken) {
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

        String[] columnNames = {"Mã GV", "Họ tên", "Học vị", "Mã Khoa", "Tên Khoa"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);

        txtMaGV = new JTextField(15);
        txtHoTen = new JTextField(25);
        txtHocVi = new JTextField(15);
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
        topPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Giảng viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Mã GV:"), gbc);
        gbc.gridx = 1;
        topPanel.add(txtMaGV, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 3;
        topPanel.add(txtHoTen, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Học vị:"), gbc);
        gbc.gridx = 1;
        topPanel.add(txtHocVi, gbc);

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
                txtMaGV.setText(tableModel.getValueAt(row, 0).toString());
                txtHoTen.setText(tableModel.getValueAt(row, 1).toString());
                txtHocVi.setText(tableModel.getValueAt(row, 2).toString());
                cboKhoa.setSelectedItem(tableModel.getValueAt(row, 3).toString());
                txtMaGV.setEditable(false);
            }
        });

        btnAdd.addActionListener(e -> addGiangVien());
        btnUpdate.addActionListener(e -> updateGiangVien());
        btnDelete.addActionListener(e -> deleteGiangVien());
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

    private void addGiangVien() {
        String maGV = txtMaGV.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String hocVi = txtHocVi.getText().trim();
        String maKhoa = (String) cboKhoa.getSelectedItem();

        if (maGV.isEmpty() || hoTen.isEmpty() || maKhoa == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        GiangVienDTO dto = new GiangVienDTO(maGV, hoTen, hocVi, maKhoa, null);
        try {
            Response response = networkClient.sendRequest(new Request(Command.GIANGVIEN_ADD, dto, authToken));
            if (response.getStatus() == Status.SUCCESS) {
                JOptionPane.showMessageDialog(this, "Thêm giảng viên thành công!");
                clearForm();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateGiangVien() {
        String maGV = txtMaGV.getText().trim();
        if (maGV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giảng viên!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        GiangVienDTO dto = new GiangVienDTO(maGV, txtHoTen.getText().trim(), txtHocVi.getText().trim(),
                                            (String) cboKhoa.getSelectedItem(), null);
        try {
            Response response = networkClient.sendRequest(new Request(Command.GIANGVIEN_UPDATE, dto, authToken));
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

    private void deleteGiangVien() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giảng viên!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maGV = tableModel.getValueAt(row, 0).toString();
        if (JOptionPane.showConfirmDialog(this, "Xóa giảng viên " + maGV + "?", "Xác nhận",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                Response response = networkClient.sendRequest(new Request(Command.GIANGVIEN_DELETE, maGV, authToken));
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
            Response response = networkClient.sendRequest(new Request(Command.GIANGVIEN_GET_ALL, null, authToken));
            if (response.getStatus() == Status.SUCCESS && response.getData() instanceof List) {
                @SuppressWarnings("unchecked")
                List<GiangVienDTO> list = (List<GiangVienDTO>) response.getData();
                tableModel.setRowCount(0);
                for (GiangVienDTO dto : list) {
                    tableModel.addRow(new Object[]{dto.getMaGV(), dto.getHoTen(), dto.getHocVi(),
                                                   dto.getMaKhoa(), dto.getTenKhoa()});
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }

    private void clearForm() {
        txtMaGV.setText("");
        txtHoTen.setText("");
        txtHocVi.setText("");
        if (cboKhoa.getItemCount() > 0) cboKhoa.setSelectedIndex(0);
        txtMaGV.setEditable(true);
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

