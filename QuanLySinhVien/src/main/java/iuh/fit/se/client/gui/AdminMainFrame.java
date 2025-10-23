package iuh.fit.se.client.gui;

import iuh.fit.se.common.dto.AuthResponseDTO;
import iuh.fit.se.common.model.UserRole;
import iuh.fit.se.common.protocol.Command;
import iuh.fit.se.common.protocol.Request;
import iuh.fit.se.common.protocol.Response;
import iuh.fit.se.client.net.NetworkClient;
import iuh.fit.se.client.gui.panels.*;

import javax.swing.*;
import java.awt.*;

/**
 * Màn hình chính cho Admin - Quản lý toàn bộ hệ thống
 */
public class AdminMainFrame extends JFrame {
    private AuthResponseDTO authResponse;
    private NetworkClient networkClient;
    private JTabbedPane tabbedPane;

    // Panels
    private KhoaPanel khoaPanel;
    private LopHocPanel lopHocPanel;
    private GiangVienPanel giangVienPanel;
    private MonHocPanel monHocPanel;
    private HocKyPanel hocKyPanel;
    private SinhVienPanel sinhVienPanel;
    private LopHocPhanPanel lopHocPhanPanel;

    public AdminMainFrame(AuthResponseDTO authResponse, NetworkClient networkClient) {
        this.authResponse = authResponse;
        this.networkClient = networkClient;

        initComponents();
        setupLayout();
        setupMenuBar();
    }

    private void initComponents() {
        setTitle("Hệ thống Quản lý Đào tạo - " + authResponse.getUsername() +
                 " (" + getRoleDisplayName() + ")");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 13));
    }

    private void setupLayout() {
        // Create panels based on role
        if (authResponse.getRole() == UserRole.ADMIN) {
            // Admin has access to all panels
            khoaPanel = new KhoaPanel(networkClient, authResponse.getAuthToken());
            lopHocPanel = new LopHocPanel(networkClient, authResponse.getAuthToken());
            giangVienPanel = new GiangVienPanel(networkClient, authResponse.getAuthToken());
            monHocPanel = new MonHocPanel(networkClient, authResponse.getAuthToken());
            hocKyPanel = new HocKyPanel(networkClient, authResponse.getAuthToken());
            sinhVienPanel = new SinhVienPanel(networkClient, authResponse.getAuthToken());
            lopHocPhanPanel = new LopHocPhanPanel(networkClient, authResponse.getAuthToken());

            tabbedPane.addTab("📚 Quản lý Khoa", khoaPanel);
            tabbedPane.addTab("🏫 Quản lý Lớp học", lopHocPanel);
            tabbedPane.addTab("👨‍🏫 Quản lý Giảng viên", giangVienPanel);
            tabbedPane.addTab("📖 Quản lý Môn học", monHocPanel);
            tabbedPane.addTab("📅 Quản lý Học kỳ", hocKyPanel);
            tabbedPane.addTab("👨‍🎓 Quản lý Sinh viên", sinhVienPanel);
            tabbedPane.addTab("🎓 Quản lý Lớp học phần", lopHocPhanPanel);
        }

        add(tabbedPane, BorderLayout.CENTER);

        // Status bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.setBackground(new Color(240, 240, 240));

        JLabel lblStatus = new JLabel("Kết nối: localhost:8888 | Người dùng: " +
                                     authResponse.getUsername() + " | Quyền: " +
                                     getRoleDisplayName());
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(lblStatus, BorderLayout.WEST);

        JLabel lblTime = new JLabel(java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        lblTime.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(lblTime, BorderLayout.EAST);

        // Update time every second
        Timer timer = new Timer(1000, e -> {
            lblTime.setText(java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        });
        timer.start();

        return panel;
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu menuFile = new JMenu("Hệ thống");
        menuFile.setMnemonic('H');

        JMenuItem itemRefresh = new JMenuItem("🔄 Làm mới");
        itemRefresh.setAccelerator(KeyStroke.getKeyStroke("F5"));
        itemRefresh.addActionListener(e -> refreshCurrentTab());

        JMenuItem itemLogout = new JMenuItem("🚪 Đăng xuất");
        itemLogout.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        itemLogout.addActionListener(e -> logout());

        JMenuItem itemExit = new JMenuItem("❌ Thoát");
        itemExit.setAccelerator(KeyStroke.getKeyStroke("alt F4"));
        itemExit.addActionListener(e -> exit());

        menuFile.add(itemRefresh);
        menuFile.addSeparator();
        menuFile.add(itemLogout);
        menuFile.add(itemExit);

        // Help menu
        JMenu menuHelp = new JMenu("Trợ giúp");
        menuHelp.setMnemonic('T');

        JMenuItem itemAbout = new JMenuItem("ℹ️ Về chương trình");
        itemAbout.addActionListener(e -> showAbout());

        menuHelp.add(itemAbout);

        menuBar.add(menuFile);
        menuBar.add(menuHelp);

        setJMenuBar(menuBar);
    }

    private void refreshCurrentTab() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        Component selectedComponent = tabbedPane.getSelectedComponent();

        if (selectedComponent instanceof RefreshablePanel) {
            ((RefreshablePanel) selectedComponent).refresh();
            JOptionPane.showMessageDialog(this,
                "Đã làm mới dữ liệu!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn đăng xuất?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            // Send logout request
            try {
                Request request = new Request(Command.LOGOUT, null, authResponse.getAuthToken());
                networkClient.sendRequest(request);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Close current frame
            dispose();

            // Open login frame
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }

    private void exit() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn thoát chương trình?",
            "Xác nhận thoát",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            // Send logout request
            try {
                Request request = new Request(Command.LOGOUT, null, authResponse.getAuthToken());
                networkClient.sendRequest(request);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            networkClient.close();
            System.exit(0);
        }
    }

    private void showAbout() {
        String message = "HỆ THỐNG QUẢN LÝ ĐÀO TẠO\n\n" +
                        "Phiên bản: 2.0 (Nâng cao)\n" +
                        "Kiến trúc: Client-Server\n" +
                        "Công nghệ: Java Socket + JPA + SQL Server\n\n" +
                        "Tính năng:\n" +
                        "• Quản lý Khoa, Lớp, Môn học\n" +
                        "• Quản lý Giảng viên, Sinh viên\n" +
                        "• Quản lý Lớp học phần\n" +
                        "• Đăng ký học phần, Nhập điểm\n" +
                        "• Phân quyền theo vai trò\n\n" +
                        "© 2024 - IUH University";

        JOptionPane.showMessageDialog(this,
            message,
            "Về chương trình",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private String getRoleDisplayName() {
        switch (authResponse.getRole()) {
            case ADMIN: return "Quản trị viên";
            case GIANG_VIEN: return "Giảng viên";
            case SINH_VIEN: return "Sinh viên";
            default: return "Không xác định";
        }
    }

    /**
     * Interface for panels that can be refreshed
     */
    public interface RefreshablePanel {
        void refresh();
    }
}

