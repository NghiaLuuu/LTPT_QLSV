package iuh.fit.se.client.gui.panels;

import iuh.fit.se.client.gui.AdminMainFrame;
import iuh.fit.se.client.net.NetworkClient;
import iuh.fit.se.common.dto.*;
import iuh.fit.se.common.protocol.Command;
import iuh.fit.se.common.protocol.Request;
import iuh.fit.se.common.protocol.Response;
import iuh.fit.se.common.protocol.Status;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LopHocPhanPanel extends JPanel implements AdminMainFrame.RefreshablePanel {
    private NetworkClient networkClient;
    private String authToken;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cboMonHoc, cboGiangVien, cboHocKy;
    private JTextField txtSoLuongToiDa;
    private JButton btnAdd, btnDelete, btnRefresh, btnClear;

    public LopHocPhanPanel(NetworkClient networkClient, String authToken) {
        this.networkClient = networkClient;
        this.authToken = authToken;
        initComponents();
        setupLayout();
        setupEventHandlers();
        loadComboBoxData();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"Mã LHP", "Môn học", "Giảng viên", "Học kỳ", "Sĩ số tối đa"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);

        cboMonHoc = new JComboBox<>();
        cboGiangVien = new JComboBox<>();
        cboHocKy = new JComboBox<>();
        txtSoLuongToiDa = new JTextField(10);

        btnAdd = new JButton("➕ Mở lớp học phần");
        btnDelete = new JButton("🗑️ Xóa");
        btnRefresh = new JButton("🔄 Làm mới");
        btnClear = new JButton("🧹 Xóa trắng");

        styleButton(btnAdd, new Color(40, 167, 69));
        styleButton(btnDelete, new Color(220, 53, 69));
        styleButton(btnRefresh, new Color(108, 117, 125));
        styleButton(btnClear, new Color(255, 193, 7));
    }

    private void setupLayout() {
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Mở lớp học phần mới"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Môn học:"), gbc);
        gbc.gridx = 1;
        topPanel.add(cboMonHoc, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Giảng viên:"), gbc);
        gbc.gridx = 3;
        topPanel.add(cboGiangVien, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Học kỳ:"), gbc);
        gbc.gridx = 1;
        topPanel.add(cboHocKy, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Sĩ số tối đa:"), gbc);
        gbc.gridx = 3;
        topPanel.add(txtSoLuongToiDa, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnRefresh);
        topPanel.add(buttonPanel, gbc);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        btnAdd.addActionListener(e -> moLopHocPhan());
        btnDelete.addActionListener(e -> deleteLopHocPhan());
        btnRefresh.addActionListener(e -> refresh());
        btnClear.addActionListener(e -> clearForm());
    }

    private void loadComboBoxData() {
        // Load Môn học
        try {
            Response response = networkClient.sendRequest(new Request(Command.MONHOC_GET_ALL, null, authToken));
            if (response.getStatus() == Status.SUCCESS && response.getData() instanceof List) {
                @SuppressWarnings("unchecked")
                List<MonHocDTO> list = (List<MonHocDTO>) response.getData();
                cboMonHoc.removeAllItems();
                for (MonHocDTO dto : list) {
                    cboMonHoc.addItem(dto.getMaMH() + " - " + dto.getTenMH());
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }

        // Load Giảng viên
        try {
            Response response = networkClient.sendRequest(new Request(Command.GIANGVIEN_GET_ALL, null, authToken));
            if (response.getStatus() == Status.SUCCESS && response.getData() instanceof List) {
                @SuppressWarnings("unchecked")
                List<GiangVienDTO> list = (List<GiangVienDTO>) response.getData();
                cboGiangVien.removeAllItems();
                for (GiangVienDTO dto : list) {
                    cboGiangVien.addItem(dto.getMaGV() + " - " + dto.getHoTen());
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }

        // Load Học kỳ
        try {
            Response response = networkClient.sendRequest(new Request(Command.HOCKY_GET_ALL, null, authToken));
            if (response.getStatus() == Status.SUCCESS && response.getData() instanceof List) {
                @SuppressWarnings("unchecked")
                List<HocKyDTO> list = (List<HocKyDTO>) response.getData();
                cboHocKy.removeAllItems();
                for (HocKyDTO dto : list) {
                    cboHocKy.addItem(dto.getMaHocKy() + " - " + dto.getTenHocKy());
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void moLopHocPhan() {
        try {
            String monHocStr = (String) cboMonHoc.getSelectedItem();
            String giangVienStr = (String) cboGiangVien.getSelectedItem();
            String hocKyStr = (String) cboHocKy.getSelectedItem();
            String soLuongStr = txtSoLuongToiDa.getText().trim();

            if (monHocStr == null || giangVienStr == null || hocKyStr == null || soLuongStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            // Extract IDs from combo box values
            String maMH = monHocStr.split(" - ")[0];
            String maGV = giangVienStr.split(" - ")[0];
            String maHocKy = hocKyStr.split(" - ")[0];
            int soLuongToiDa = Integer.parseInt(soLuongStr);

            // Create data map
            Map<String, Object> data = new HashMap<>();
            data.put("maMH", maMH);
            data.put("maGV", maGV);
            data.put("maHocKy", maHocKy);
            data.put("soLuongToiDa", soLuongToiDa);

            Response response = networkClient.sendRequest(new Request(Command.LOPHOCPHAN_ADD, data, authToken));

            if (response.getStatus() == Status.SUCCESS) {
                JOptionPane.showMessageDialog(this, "Mở lớp học phần thành công!");
                clearForm();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteLopHocPhan() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học phần!");
            return;
        }

        Long maLHP = Long.parseLong(tableModel.getValueAt(row, 0).toString());
        if (JOptionPane.showConfirmDialog(this, "Xóa lớp học phần " + maLHP + "?", "Xác nhận",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                Response response = networkClient.sendRequest(new Request(Command.LOPHOCPHAN_DELETE, maLHP, authToken));
                if (response.getStatus() == Status.SUCCESS) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
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
        loadComboBoxData();
    }

    private void loadData() {
        try {
            // Get all học kỳ first
            Response hkResponse = networkClient.sendRequest(new Request(Command.HOCKY_GET_ALL, null, authToken));
            if (hkResponse.getStatus() == Status.SUCCESS && hkResponse.getData() instanceof List) {
                @SuppressWarnings("unchecked")
                List<HocKyDTO> hkList = (List<HocKyDTO>) hkResponse.getData();

                tableModel.setRowCount(0);

                // Load lớp học phần for each học kỳ
                for (HocKyDTO hk : hkList) {
                    Response lhpResponse = networkClient.sendRequest(
                        new Request(Command.LOPHOCPHAN_GET_ALL_BY_HOCKY, hk.getMaHocKy(), authToken));

                    if (lhpResponse.getStatus() == Status.SUCCESS && lhpResponse.getData() instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<LopHocPhanDTO> lhpList = (List<LopHocPhanDTO>) lhpResponse.getData();

                        for (LopHocPhanDTO dto : lhpList) {
                            tableModel.addRow(new Object[]{
                                dto.getMaLHP(),
                                dto.getTenMH(),
                                dto.getTenGV(),
                                dto.getTenHocKy(),
                                dto.getSoLuongToiDa()
                            });
                        }
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }

    private void clearForm() {
        if (cboMonHoc.getItemCount() > 0) cboMonHoc.setSelectedIndex(0);
        if (cboGiangVien.getItemCount() > 0) cboGiangVien.setSelectedIndex(0);
        if (cboHocKy.getItemCount() > 0) cboHocKy.setSelectedIndex(0);
        txtSoLuongToiDa.setText("");
        table.clearSelection();
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(150, 35));
    }
}

