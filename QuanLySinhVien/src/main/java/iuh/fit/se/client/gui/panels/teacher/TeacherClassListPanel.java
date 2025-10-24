package iuh.fit.se.client.gui.panels.teacher;

import iuh.fit.se.client.gui.TeacherMainFrame;
import iuh.fit.se.client.net.NetworkClient;
import iuh.fit.se.common.dto.AuthResponseDTO;
import iuh.fit.se.common.dto.LopHocPhanDTO;
import iuh.fit.se.common.protocol.Command;
import iuh.fit.se.common.protocol.Request;
import iuh.fit.se.common.protocol.Response;
import iuh.fit.se.common.protocol.Status;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel hiển thị danh sách lớp học phần của giảng viên
 */
public class TeacherClassListPanel extends JPanel implements TeacherMainFrame.RefreshablePanel {
    private NetworkClient networkClient;
    private AuthResponseDTO authResponse;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblStatus;

    public TeacherClassListPanel(NetworkClient networkClient, AuthResponseDTO authResponse) {
        this.networkClient = networkClient;
        this.authResponse = authResponse;
        initComponents();
        setupLayout();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"Mã LHP", "Tên môn học", "Học kỳ", "Năm học", "Thứ", "Tiết", "Phòng", "Sĩ số"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    private void setupLayout() {
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(41, 128, 185));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("📚 DANH SÁCH LỚP HỌC PHẦN CỦA TÔI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        titlePanel.add(lblTitle, BorderLayout.WEST);

        JButton btnRefresh = new JButton("🔄 Làm mới");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefresh.setBackground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> loadData());
        titlePanel.add(btnRefresh, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        // Table
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách lớp"));
        add(scrollPane, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblStatus = new JLabel("Sẵn sàng");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusPanel.add(lblStatus);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        lblStatus.setText("⏳ Đang tải dữ liệu...");

        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() throws Exception {
                Request request = new Request(Command.GV_GET_MY_CLASSES,
                    authResponse.getMaGV(),
                    authResponse.getAuthToken());
                return networkClient.sendRequest(request);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.getStatus() == Status.SUCCESS) {
                        @SuppressWarnings("unchecked")
                        List<LopHocPhanDTO> classes = (List<LopHocPhanDTO>) response.getData();
                        loadDataToTable(classes);
                        lblStatus.setText("✓ Đã tải " + classes.size() + " lớp học phần");
                    } else {
                        lblStatus.setText("✗ Lỗi: " + response.getMessage());
                        JOptionPane.showMessageDialog(TeacherClassListPanel.this,
                            response.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    lblStatus.setText("✗ Lỗi kết nối");
                    JOptionPane.showMessageDialog(TeacherClassListPanel.this,
                        "Không thể tải dữ liệu: " + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void loadDataToTable(List<LopHocPhanDTO> classes) {
        tableModel.setRowCount(0);
        for (LopHocPhanDTO lhp : classes) {
            Object[] row = {
                lhp.getMaLHP(),
                lhp.getTenMH(),
                lhp.getTenHocKy(),
                lhp.getNamHoc(),
                lhp.getThu(),
                lhp.getTiet(),
                lhp.getPhongHoc(),
                lhp.getSiSo()
            };
            tableModel.addRow(row);
        }
    }

    @Override
    public void refresh() {
        loadData();
    }
}

