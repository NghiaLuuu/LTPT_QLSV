package iuh.fit.se.client.gui.panels;

import iuh.fit.se.client.gui.AdminMainFrame;
import iuh.fit.se.client.net.NetworkClient;
import iuh.fit.se.common.model.Gender;
import iuh.fit.se.common.model.SinhVienDTO;
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

public class SinhVienPanel extends JPanel implements AdminMainFrame.RefreshablePanel {
    private NetworkClient networkClient;
    private String authToken;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaSV, txtHoTen, txtNgaySinh, txtChuyenNganh, txtDiemTB;
    private JComboBox<String> cboGioiTinh;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnClear;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public SinhVienPanel(NetworkClient networkClient, String authToken) {
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

        String[] columnNames = {"Mã SV", "Họ tên", "Ngày sinh", "Giới tính", "Chuyên ngành", "Điểm TB"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);

        txtMaSV = new JTextField(15);
        txtHoTen = new JTextField(25);
        txtNgaySinh = new JTextField(12);
        txtChuyenNganh = new JTextField(20);
        txtDiemTB = new JTextField(10);
        cboGioiTinh = new JComboBox<>(new String[]{"NAM", "NU"});

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
        topPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Sinh viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Mã SV:"), gbc);
        gbc.gridx = 1;
        topPanel.add(txtMaSV, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 3;
        topPanel.add(txtHoTen, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Ngày sinh (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        topPanel.add(txtNgaySinh, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 3;
        topPanel.add(cboGioiTinh, gbc);

        // Row 3
        gbc.gridx = 0; gbc.gridy = 2;
        topPanel.add(new JLabel("Chuyên ngành:"), gbc);
        gbc.gridx = 1;
        topPanel.add(txtChuyenNganh, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Điểm TB:"), gbc);
        gbc.gridx = 3;
        topPanel.add(txtDiemTB, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 3;
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
                txtMaSV.setText(tableModel.getValueAt(row, 0).toString());
                txtHoTen.setText(tableModel.getValueAt(row, 1).toString());
                txtNgaySinh.setText(tableModel.getValueAt(row, 2).toString());
                cboGioiTinh.setSelectedItem(tableModel.getValueAt(row, 3).toString());
                txtChuyenNganh.setText(tableModel.getValueAt(row, 4).toString());
                txtDiemTB.setText(tableModel.getValueAt(row, 5).toString());
                txtMaSV.setEditable(false);
            }
        });

        btnAdd.addActionListener(e -> addSinhVien());
        btnUpdate.addActionListener(e -> updateSinhVien());
        btnDelete.addActionListener(e -> deleteSinhVien());
        btnRefresh.addActionListener(e -> refresh());
        btnClear.addActionListener(e -> clearForm());
    }

    private void addSinhVien() {
        try {
            String maSV = txtMaSV.getText().trim();
            String hoTen = txtHoTen.getText().trim();
            LocalDate ngaySinh = LocalDate.parse(txtNgaySinh.getText().trim(), formatter);
            Gender gioiTinh = Gender.valueOf((String) cboGioiTinh.getSelectedItem());
            String chuyenNganh = txtChuyenNganh.getText().trim();
            double diemTB = Double.parseDouble(txtDiemTB.getText().trim());

            if (maSV.isEmpty() || hoTen.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            SinhVienDTO dto = new SinhVienDTO(maSV, hoTen, ngaySinh, gioiTinh, chuyenNganh, null, diemTB);
            Response response = networkClient.sendRequest(new Request(Command.SINHVIEN_ADD, dto, authToken));

            if (response.getStatus() == Status.SUCCESS) {
                JOptionPane.showMessageDialog(this, "Thêm sinh viên thành công!");
                clearForm();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSinhVien() {
        try {
            String maSV = txtMaSV.getText().trim();
            if (maSV.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên!");
                return;
            }

            String hoTen = txtHoTen.getText().trim();
            LocalDate ngaySinh = LocalDate.parse(txtNgaySinh.getText().trim(), formatter);
            Gender gioiTinh = Gender.valueOf((String) cboGioiTinh.getSelectedItem());
            String chuyenNganh = txtChuyenNganh.getText().trim();
            double diemTB = Double.parseDouble(txtDiemTB.getText().trim());

            SinhVienDTO dto = new SinhVienDTO(maSV, hoTen, ngaySinh, gioiTinh, chuyenNganh, null, diemTB);
            Response response = networkClient.sendRequest(new Request(Command.SINHVIEN_UPDATE, dto, authToken));

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

    private void deleteSinhVien() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên!");
            return;
        }

        String maSV = tableModel.getValueAt(row, 0).toString();
        if (JOptionPane.showConfirmDialog(this, "Xóa sinh viên " + maSV + "?", "Xác nhận",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                Response response = networkClient.sendRequest(new Request(Command.SINHVIEN_DELETE, maSV, authToken));
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
            Response response = networkClient.sendRequest(new Request(Command.GET_ALL_STUDENTS, null, authToken));
            if (response.getStatus() == Status.SUCCESS && response.getData() instanceof List) {
                @SuppressWarnings("unchecked")
                List<SinhVienDTO> list = (List<SinhVienDTO>) response.getData();
                tableModel.setRowCount(0);
                for (SinhVienDTO dto : list) {
                    tableModel.addRow(new Object[]{
                        dto.getMaSV(),
                        dto.getHoTen(),
                        dto.getNgaySinh().format(formatter),
                        dto.getGioiTinh(),
                        dto.getChuyenNganh(),
                        dto.getDiemTB()
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }

    private void clearForm() {
        txtMaSV.setText("");
        txtHoTen.setText("");
        txtNgaySinh.setText("");
        txtChuyenNganh.setText("");
        txtDiemTB.setText("");
        cboGioiTinh.setSelectedIndex(0);
        txtMaSV.setEditable(true);
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

