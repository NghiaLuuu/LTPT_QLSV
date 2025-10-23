package iuh.fit.se.client.gui.panels;

import iuh.fit.se.client.gui.AdminMainFrame;
import iuh.fit.se.client.net.NetworkClient;
import iuh.fit.se.common.dto.KhoaDTO;
import iuh.fit.se.common.dto.LopHocDTO;
import iuh.fit.se.common.protocol.Command;
import iuh.fit.se.common.protocol.Request;
import iuh.fit.se.common.protocol.Response;
import iuh.fit.se.common.protocol.Status;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LopHocPanel extends JPanel implements AdminMainFrame.RefreshablePanel {
    private NetworkClient networkClient;
    private String authToken;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaLop, txtTenLop, txtNienKhoa;
    private JComboBox<String> cboKhoa;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnClear;

    public LopHocPanel(NetworkClient networkClient, String authToken) {
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

        String[] columnNames = {"Mã Lớp", "Tên Lớp", "Niên khóa", "Mã Khoa", "Tên Khoa"};
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

        txtMaLop = new JTextField(15);
        txtTenLop = new JTextField(25);
        txtNienKhoa = new JTextField(12);
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
        topPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Lớp học"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Mã Lớp:"), gbc);
        gbc.gridx = 1;
        topPanel.add(txtMaLop, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Tên Lớp:"), gbc);
        gbc.gridx = 3;
        topPanel.add(txtTenLop, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Niên khóa:"), gbc);
        gbc.gridx = 1;
        topPanel.add(txtNienKhoa, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Khoa:"), gbc);
        gbc.gridx = 3;
        topPanel.add(cboKhoa, gbc);

        // Buttons
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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách Lớp học"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    txtMaLop.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    txtTenLop.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    txtNienKhoa.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    cboKhoa.setSelectedItem(tableModel.getValueAt(selectedRow, 3).toString());
                    txtMaLop.setEditable(false);
                }
            }
        });

        btnAdd.addActionListener(e -> addLopHoc());
        btnUpdate.addActionListener(e -> updateLopHoc());
        btnDelete.addActionListener(e -> deleteLopHoc());
        btnRefresh.addActionListener(e -> refresh());
        btnClear.addActionListener(e -> clearForm());
    }

    private void loadKhoa() {
        Request request = new Request(Command.KHOA_GET_ALL, null, authToken);
        try {
            Response response = networkClient.sendRequest(request);
            if (response.getStatus() == Status.SUCCESS && response.getData() instanceof List) {
                @SuppressWarnings("unchecked")
                List<KhoaDTO> list = (List<KhoaDTO>) response.getData();
                cboKhoa.removeAllItems();
                for (KhoaDTO dto : list) {
                    cboKhoa.addItem(dto.getMaKhoa());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addLopHoc() {
        String maLop = txtMaLop.getText().trim();
        String tenLop = txtTenLop.getText().trim();
        String nienKhoa = txtNienKhoa.getText().trim();
        String maKhoa = (String) cboKhoa.getSelectedItem();

        if (maLop.isEmpty() || tenLop.isEmpty() || nienKhoa.isEmpty() || maKhoa == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LopHocDTO dto = new LopHocDTO(maLop, tenLop, nienKhoa, maKhoa, null);
        Request request = new Request(Command.LOPHOC_ADD, dto, authToken);

        try {
            Response response = networkClient.sendRequest(request);
            if (response.getStatus() == Status.SUCCESS) {
                JOptionPane.showMessageDialog(this, "Thêm lớp học thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateLopHoc() {
        String maLop = txtMaLop.getText().trim();
        String tenLop = txtTenLop.getText().trim();
        String nienKhoa = txtNienKhoa.getText().trim();
        String maKhoa = (String) cboKhoa.getSelectedItem();

        if (maLop.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp cần cập nhật!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LopHocDTO dto = new LopHocDTO(maLop, tenLop, nienKhoa, maKhoa, null);
        Request request = new Request(Command.LOPHOC_UPDATE, dto, authToken);

        try {
            Response response = networkClient.sendRequest(request);
            if (response.getStatus() == Status.SUCCESS) {
                JOptionPane.showMessageDialog(this, "Cập nhật lớp học thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteLopHoc() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maLop = tableModel.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa lớp " + maLop + "?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Request request = new Request(Command.LOPHOC_DELETE, maLop, authToken);

            try {
                Response response = networkClient.sendRequest(request);
                if (response.getStatus() == Status.SUCCESS) {
                    JOptionPane.showMessageDialog(this, "Xóa lớp học thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void refresh() {
        loadData();
        loadKhoa();
    }

    private void loadData() {
        Request request = new Request(Command.LOPHOC_GET_ALL, null, authToken);

        try {
            Response response = networkClient.sendRequest(request);
            if (response.getStatus() == Status.SUCCESS && response.getData() instanceof List) {
                @SuppressWarnings("unchecked")
                List<LopHocDTO> list = (List<LopHocDTO>) response.getData();

                tableModel.setRowCount(0);
                for (LopHocDTO dto : list) {
                    tableModel.addRow(new Object[]{
                        dto.getMaLop(), dto.getTenLop(), dto.getNienKhoa(),
                        dto.getMaKhoa(), dto.getTenKhoa()
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtMaLop.setText("");
        txtTenLop.setText("");
        txtNienKhoa.setText("");
        cboKhoa.setSelectedIndex(0);
        txtMaLop.setEditable(true);
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

