package iuh.fit.se.client.gui.panels.student;

import iuh.fit.se.client.gui.StudentMainFrame;
import iuh.fit.se.client.net.NetworkClient;
import iuh.fit.se.common.dto.AuthResponseDTO;
import iuh.fit.se.common.dto.SinhVienDTO;
import iuh.fit.se.common.protocol.Command;
import iuh.fit.se.common.protocol.Request;
import iuh.fit.se.common.protocol.Response;
import iuh.fit.se.common.protocol.Status;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Panel hiển thị thông tin cá nhân sinh viên
 */
public class StudentProfilePanel extends JPanel implements StudentMainFrame.RefreshablePanel {
    private NetworkClient networkClient;
    private AuthResponseDTO authResponse;
    private JLabel lblHoTen, lblMaSV, lblNgaySinh, lblGioiTinh, lblChuyenNganh, lblLop, lblDiemTB;

    public StudentProfilePanel(NetworkClient networkClient, AuthResponseDTO authResponse) {
        this.networkClient = networkClient;
        this.authResponse = authResponse;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(41, 128, 185));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("👤 THÔNG TIN CÁ NHÂN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        titlePanel.add(lblTitle);

        add(titlePanel, BorderLayout.NORTH);

        // Info panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(30, 30, 30, 30),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                "Chi tiết thông tin",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14)
            )
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Initialize labels with default values
        lblMaSV = createValueLabel("...");
        lblHoTen = createValueLabel("...");
        lblNgaySinh = createValueLabel("...");
        lblGioiTinh = createValueLabel("...");
        lblChuyenNganh = createValueLabel("...");
        lblLop = createValueLabel("...");
        lblDiemTB = createValueLabel("...");

        // Add fields
        int row = 0;
        addInfoRow(infoPanel, gbc, row++, "Mã sinh viên:", lblMaSV, "🎫");
        addInfoRow(infoPanel, gbc, row++, "Họ và tên:", lblHoTen, "👤");
        addInfoRow(infoPanel, gbc, row++, "Ngày sinh:", lblNgaySinh, "📅");
        addInfoRow(infoPanel, gbc, row++, "Giới tính:", lblGioiTinh, "⚥");
        addInfoRow(infoPanel, gbc, row++, "Chuyên ngành:", lblChuyenNganh, "📚");
        addInfoRow(infoPanel, gbc, row++, "Lớp:", lblLop, "🏫");
        addInfoRow(infoPanel, gbc, row++, "Điểm TB:", lblDiemTB, "🎓");

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(236, 240, 241));
        centerPanel.add(infoPanel);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void addInfoRow(JPanel panel, GridBagConstraints gbc, int row, String label, JLabel valueLabel, String icon) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        panel.add(iconLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        JLabel lblKey = new JLabel(label);
        lblKey.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblKey.setForeground(new Color(52, 73, 94));
        panel.add(lblKey, gbc);

        gbc.gridx = 2;
        gbc.weightx = 1;
        panel.add(valueLabel, gbc);
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setForeground(new Color(44, 62, 80));
        return label;
    }

    private void loadData() {
        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() throws Exception {
                Request request = new Request(Command.SINHVIEN_GET_BY_ID,
                    authResponse.getMaSV(),
                    authResponse.getAuthToken());
                return networkClient.sendRequest(request);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.getStatus() == Status.SUCCESS) {
                        SinhVienDTO sv = (SinhVienDTO) response.getData();
                        updateDisplay(sv);
                    } else {
                        JOptionPane.showMessageDialog(StudentProfilePanel.this,
                            "Không thể tải thông tin: " + response.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StudentProfilePanel.this,
                        "Lỗi kết nối: " + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void updateDisplay(SinhVienDTO sv) {
        lblMaSV.setText(sv.getMaSV());
        lblHoTen.setText(sv.getHoTen());
        lblNgaySinh.setText(sv.getNgaySinh() != null ?
            sv.getNgaySinh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A");
        lblGioiTinh.setText(sv.getGioiTinh() != null ? sv.getGioiTinh().toString() : "N/A");
        lblChuyenNganh.setText(sv.getChuyenNganh() != null ? sv.getChuyenNganh() : "N/A");
        lblLop.setText(sv.getLop() != null ? sv.getLop() : "N/A");
        lblDiemTB.setText(String.format("%.2f", sv.getDiemTB()));
    }

    @Override
    public void refresh() {
        loadData();
    }
}
