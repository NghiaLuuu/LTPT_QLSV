package iuh.fit.se.client.gui;

import iuh.fit.se.common.dto.AuthResponseDTO;
import iuh.fit.se.client.net.NetworkClient;
import iuh.fit.se.client.gui.panels.student.StudentProfilePanel;
import iuh.fit.se.client.gui.panels.student.StudentCourseRegistrationPanel;
import iuh.fit.se.client.gui.panels.student.StudentRegisteredCoursesPanel;
import iuh.fit.se.client.gui.panels.student.StudentTranscriptPanel;
import iuh.fit.se.common.protocol.Command;
import iuh.fit.se.common.protocol.Request;

import javax.swing.*;
import java.awt.*;

/**
 * Màn hình chính cho Sinh viên
 */
public class StudentMainFrame extends JFrame {
    private AuthResponseDTO authResponse;
    private NetworkClient networkClient;
    private JTabbedPane tabbedPane;

    // Modern colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color HEADER_BG = new Color(52, 73, 94);
    private static final Color STATUS_BAR_BG = new Color(44, 62, 80);
    private static final Color TAB_BG = new Color(236, 240, 241);

    // Panels
    private StudentProfilePanel profilePanel;
    private StudentCourseRegistrationPanel registrationPanel;
    private StudentRegisteredCoursesPanel registeredCoursesPanel;
    private StudentTranscriptPanel transcriptPanel;

    public StudentMainFrame(AuthResponseDTO authResponse, NetworkClient networkClient) {
        this.authResponse = authResponse;
        this.networkClient = networkClient;

        initComponents();
        setupLayout();
        setupMenuBar();
    }

    private void initComponents() {
        setTitle("🎓 Hệ thống Quản lý Đào tạo - Sinh viên: " + authResponse.getUsername());
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set modern look and feel
        try {
            UIManager.put("TabbedPane.contentBorderInsets", new Insets(10, 10, 10, 10));
            UIManager.put("TabbedPane.selected", PRIMARY_COLOR);
            UIManager.put("TabbedPane.foreground", HEADER_BG);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(TAB_BG);
        tabbedPane.setForeground(HEADER_BG);
    }

    private void setupLayout() {
        getContentPane().setBackground(Color.WHITE);

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Create panels for student
        profilePanel = new StudentProfilePanel(networkClient, authResponse);
        registrationPanel = new StudentCourseRegistrationPanel(networkClient, authResponse);
        registeredCoursesPanel = new StudentRegisteredCoursesPanel(networkClient, authResponse);
        transcriptPanel = new StudentTranscriptPanel(networkClient, authResponse);

        tabbedPane.addTab("  👤 Thông tin cá nhân  ", profilePanel);
        tabbedPane.addTab("  📅 Đăng ký học phần  ", registrationPanel);
        tabbedPane.addTab("  📖 Lớp đã đăng ký  ", registeredCoursesPanel);
        tabbedPane.addTab("  🎓 Bảng điểm  ", transcriptPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Status bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Left side - Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(HEADER_BG);

        JLabel iconLabel = new JLabel("👨‍🎓");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(HEADER_BG);

        JLabel titleLabel = new JLabel("SINH VIÊN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Quản lý học tập và đăng ký học phần");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(189, 195, 199));

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        leftPanel.add(iconLabel);
        leftPanel.add(titlePanel);

        // Right side - User info
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(HEADER_BG);

        JLabel userLabel = new JLabel("👤 " + authResponse.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);

        JLabel roleLabel = new JLabel("• Mã SV: " + authResponse.getMaSV());
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleLabel.setForeground(new Color(46, 204, 113));

        rightPanel.add(userLabel);
        rightPanel.add(roleLabel);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        panel.setBackground(STATUS_BAR_BG);

        // Left side
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(STATUS_BAR_BG);

        JLabel lblConnection = new JLabel("🌐 Kết nối: localhost:8888");
        lblConnection.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblConnection.setForeground(new Color(46, 204, 113));

        JLabel lblStatus = new JLabel("• Trạng thái: Hoạt động");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(Color.WHITE);

        leftPanel.add(lblConnection);
        leftPanel.add(lblStatus);

        // Right side - Time
        JLabel lblTime = new JLabel(java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTime.setForeground(new Color(189, 195, 199));

        // Update time every second
        Timer timer = new Timer(1000, e -> {
            lblTime.setText(java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        });
        timer.start();

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(lblTime, BorderLayout.EAST);

        return panel;
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(189, 195, 199)));

        // File menu
        JMenu menuFile = new JMenu("  Hệ thống  ");
        menuFile.setFont(new Font("Segoe UI", Font.BOLD, 13));
        menuFile.setMnemonic('H');

        JMenuItem itemRefresh = new JMenuItem("🔄 Làm mới");
        itemRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemRefresh.setAccelerator(KeyStroke.getKeyStroke("F5"));
        itemRefresh.addActionListener(e -> refreshCurrentTab());

        JMenuItem itemChangePassword = new JMenuItem("🔑 Đổi mật khẩu");
        itemChangePassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemChangePassword.addActionListener(e -> changePassword());

        JMenuItem itemLogout = new JMenuItem("🚪 Đăng xuất");
        itemLogout.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemLogout.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        itemLogout.addActionListener(e -> logout());

        JMenuItem itemExit = new JMenuItem("❌ Thoát");
        itemExit.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemExit.setAccelerator(KeyStroke.getKeyStroke("alt F4"));
        itemExit.addActionListener(e -> exit());

        menuFile.add(itemRefresh);
        menuFile.add(itemChangePassword);
        menuFile.addSeparator();
        menuFile.add(itemLogout);
        menuFile.add(itemExit);

        // Help menu
        JMenu menuHelp = new JMenu("  Trợ giúp  ");
        menuHelp.setFont(new Font("Segoe UI", Font.BOLD, 13));
        menuHelp.setMnemonic('T');

        JMenuItem itemAbout = new JMenuItem("ℹ️ Về chương trình");
        itemAbout.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemAbout.addActionListener(e -> showAbout());

        menuHelp.add(itemAbout);

        menuBar.add(menuFile);
        menuBar.add(menuHelp);

        setJMenuBar(menuBar);
    }

    private void refreshCurrentTab() {
        Component selectedComponent = tabbedPane.getSelectedComponent();

        if (selectedComponent instanceof RefreshablePanel) {
            ((RefreshablePanel) selectedComponent).refresh();
            JOptionPane.showMessageDialog(this,
                "Đã làm mới dữ liệu!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void changePassword() {
        JOptionPane.showMessageDialog(this,
            "Chức năng đổi mật khẩu sẽ được bổ sung sau!",
            "Thông báo",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn đăng xuất?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                Request request = new Request(Command.LOGOUT, null, authResponse.getAuthToken());
                networkClient.sendRequest(request);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            dispose();

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
        JDialog dialog = new JDialog(this, "Về chương trình", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel("👨‍🎓");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("SINH VIÊN - HỆ THỐNG QUẢN LÝ ĐÀO TẠO");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(HEADER_BG);

        JTextArea textArea = new JTextArea();
        textArea.setText("\nPhiên bản: 2.0\n" +
                        "Chức năng:\n" +
                        "• Xem thông tin cá nhân\n" +
                        "• Đăng ký học phần\n" +
                        "• Xem bảng điểm\n" +
                        "• Theo dõi kết quả học tập\n\n" +
                        "© 2024 - IUH University");
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textArea.setEditable(false);
        textArea.setBackground(Color.WHITE);
        textArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton okButton = new JButton("Đóng");
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okButton.setBackground(PRIMARY_COLOR);
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.setBorderPainted(false);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        okButton.setPreferredSize(new Dimension(120, 40));
        okButton.addActionListener(e -> dialog.dispose());

        panel.add(iconLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(textArea);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(okButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * Interface để các panel có thể refresh
     */
    public interface RefreshablePanel {
        void refresh();
    }
}
