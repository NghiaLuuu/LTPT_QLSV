package iuh.fit.se.client.gui;

import iuh.fit.se.client.net.NetworkClient;
import iuh.fit.se.common.dto.SinhVienDTO;
import iuh.fit.se.common.model.Gender;
import iuh.fit.se.common.protocol.Command;
import iuh.fit.se.common.protocol.Request;
import iuh.fit.se.common.protocol.Response;
import iuh.fit.se.common.protocol.Status;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StudentDialog extends JDialog {
    private NetworkClient networkClient;
    private SinhVienDTO student; // null = Thêm mới, not null = Sửa
    private boolean success = false;

    // Form components
    private JTextField txtMaSV;
    private JTextField txtHoTen;
    private JTextField txtNgaySinh;
    private JComboBox<Gender> cbGender;
    private JTextField txtChuyenNganh;
    private JTextField txtLop;
    private JSpinner spnDiemTB;

    private JButton btnSave;
    private JButton btnCancel;

    public StudentDialog(Frame parent, NetworkClient networkClient, SinhVienDTO student) {
        super(parent, student == null ? "Thêm Sinh viên" : "Sửa Sinh viên", true);
        this.networkClient = networkClient;
        this.student = student;

        initComponents();
        setupLayout();
        setupListeners();

        if (student != null) {
            populateData();
        }
    }

    private void initComponents() {
        // Form fields
        txtMaSV = new JTextField(20);
        txtHoTen = new JTextField(20);
        txtNgaySinh = new JTextField(20);

        cbGender = new JComboBox<>(Gender.values());

        txtChuyenNganh = new JTextField(20);
        txtLop = new JTextField(20);

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0.0, 0.0, 10.0, 0.1);
        spnDiemTB = new JSpinner(spinnerModel);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spnDiemTB, "0.0");
        spnDiemTB.setEditor(editor);

        btnSave = new JButton(student == null ? "Lưu" : "Cập nhật");
        btnCancel = new JButton("Hủy bỏ");

        // Nếu đang sửa, disable txtMaSV
        if (student != null) {
            txtMaSV.setEnabled(false);
            txtMaSV.setBackground(Color.LIGHT_GRAY);
        }
    }

    private void setupLayout() {
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Mã SV
        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(new JLabel("Mã SV:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        pnlForm.add(txtMaSV, gbc);

        // Họ tên
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        pnlForm.add(new JLabel("Họ Tên:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        pnlForm.add(txtHoTen, gbc);

        // Ngày sinh
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        pnlForm.add(new JLabel("Ngày Sinh (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        pnlForm.add(txtNgaySinh, gbc);

        // Giới tính
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        pnlForm.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        pnlForm.add(cbGender, gbc);

        // Chuyên ngành
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        pnlForm.add(new JLabel("Chuyên ngành:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        pnlForm.add(txtChuyenNganh, gbc);

        // Lớp
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        pnlForm.add(new JLabel("Lớp:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        pnlForm.add(txtLop, gbc);

        // Điểm TB
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        pnlForm.add(new JLabel("Điểm TB (0-10):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        pnlForm.add(spnDiemTB, gbc);

        // Panel buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pnlButtons.add(btnSave);
        pnlButtons.add(btnCancel);

        // Main layout
        setLayout(new BorderLayout());
        add(pnlForm, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    private void setupListeners() {
        btnSave.addActionListener(e -> saveStudent());
        btnCancel.addActionListener(e -> {
            success = false;
            dispose();
        });
    }

    private void populateData() {
        txtMaSV.setText(student.getMaSV());
        txtHoTen.setText(student.getHoTen());

        if (student.getNgaySinh() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            txtNgaySinh.setText(student.getNgaySinh().format(formatter));
        }

        cbGender.setSelectedItem(student.getGioiTinh());
        txtChuyenNganh.setText(student.getChuyenNganh());
        txtLop.setText(student.getLop());
        spnDiemTB.setValue(student.getDiemTB());
    }

    private void saveStudent() {
        // Validation
        String maSV = txtMaSV.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String ngaySinhStr = txtNgaySinh.getText().trim();
        String chuyenNganh = txtChuyenNganh.getText().trim();
        String lop = txtLop.getText().trim();

        if (maSV.isEmpty()) {
            showError("Mã SV không được để trống!");
            txtMaSV.requestFocus();
            return;
        }

        if (maSV.length() < 5 || maSV.length() > 20) {
            showError("Mã SV phải từ 5 đến 20 ký tự!");
            txtMaSV.requestFocus();
            return;
        }

        if (hoTen.isEmpty()) {
            showError("Họ tên không được để trống!");
            txtHoTen.requestFocus();
            return;
        }

        if (ngaySinhStr.isEmpty()) {
            showError("Ngày sinh không được để trống!");
            txtNgaySinh.requestFocus();
            return;
        }

        LocalDate ngaySinh;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            ngaySinh = LocalDate.parse(ngaySinhStr, formatter);

            if (ngaySinh.isAfter(LocalDate.now())) {
                showError("Ngày sinh phải là một ngày trong quá khứ!");
                txtNgaySinh.requestFocus();
                return;
            }
        } catch (DateTimeParseException e) {
            showError("Định dạng ngày sinh không hợp lệ! Vui lòng nhập theo định dạng dd/MM/yyyy (VD: 01/01/2000)");
            txtNgaySinh.requestFocus();
            return;
        }

        Gender gioiTinh = (Gender) cbGender.getSelectedItem();
        double diemTB = (Double) spnDiemTB.getValue();

        // Tạo DTO
        SinhVienDTO svDTO = new SinhVienDTO(maSV, hoTen, ngaySinh, gioiTinh, chuyenNganh, lop, diemTB);

        // Gửi request bằng SwingWorker
        btnSave.setEnabled(false);
        btnCancel.setEnabled(false);

        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() throws Exception {
                Command command = student == null ? Command.ADD_STUDENT : Command.UPDATE_STUDENT;
                Request request = new Request(command, svDTO);
                return networkClient.sendRequest(request);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.getStatus() == Status.SUCCESS) {
                        success = true;
                        JOptionPane.showMessageDialog(StudentDialog.this,
                                response.getMessage(),
                                "Thành công",
                                JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        success = false;
                        JOptionPane.showMessageDialog(StudentDialog.this,
                                response.getMessage(),
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                        btnSave.setEnabled(true);
                        btnCancel.setEnabled(true);
                    }
                } catch (Exception e) {
                    success = false;
                    JOptionPane.showMessageDialog(StudentDialog.this,
                            "Lỗi kết nối: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    btnSave.setEnabled(true);
                    btnCancel.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Lỗi nhập liệu",
                JOptionPane.ERROR_MESSAGE);
    }

    public boolean isSuccess() {
        return success;
    }
}
