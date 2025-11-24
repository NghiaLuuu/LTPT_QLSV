package iuh.fit.se.gui.view;

import com.fasterxml.jackson.databind.JsonNode;
import iuh.fit.se.dto.response.StudentResponse;
import iuh.fit.se.dto.response.StudentDashboardResponse;
import iuh.fit.se.gui.component.ModernButton;
import iuh.fit.se.gui.util.ApiClient;
import iuh.fit.se.gui.util.AppTheme;
import iuh.fit.se.gui.util.WebSocketClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Dashboard dành cho sinh viên
 */
public class StudentDashboardFrame extends JFrame {

    private JPanel contentPanel;
    private JLabel lblWelcome;
    private JsonNode studentData;
    private WebSocketClient webSocketClient;
    private JPanel currentInfoCard; // Reference to info card để update real-time

    public StudentDashboardFrame() {
        initComponents();
        loadStudentData();
        setupWebSocket(); // Kết nối WebSocket
        setTitle("Trang Sinh Viên - Hệ Thống Quản Lý Sinh Viên");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // Disconnect WebSocket khi đóng cửa sổ
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (webSocketClient != null) {
                    webSocketClient.disconnect();
                }
            }
        });
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Top navigation bar
        JPanel navBar = createNavBar();
        add(navBar, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Content area
        contentPanel = new JPanel();
        contentPanel.setBackground(AppTheme.BACKGROUND_COLOR);
        contentPanel.setLayout(new BorderLayout());

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createNavBar() {
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(AppTheme.PRIMARY_COLOR);
        navBar.setPreferredSize(new Dimension(0, 60));
        navBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Logo and title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);

        JLabel lblLogo = new JLabel("🎓 ");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        lblLogo.setForeground(Color.WHITE);

        JLabel lblTitle = new JLabel("TRANG SINH VIÊN");
        lblTitle.setFont(AppTheme.HEADING_FONT);
        lblTitle.setForeground(Color.WHITE);

        leftPanel.add(lblLogo);
        leftPanel.add(lblTitle);

        // User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        lblWelcome = new JLabel("👤 " + ApiClient.getCurrentUsername());
        lblWelcome.setFont(AppTheme.NORMAL_FONT);
        lblWelcome.setForeground(Color.WHITE);

        ModernButton btnLogout = new ModernButton("Đăng xuất");
        btnLogout.setBackground(AppTheme.DANGER_COLOR);
        btnLogout.setPreferredSize(new Dimension(120, 35));
        btnLogout.addActionListener(e -> logout());

        rightPanel.add(lblWelcome);
        rightPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        rightPanel.add(btnLogout);

        navBar.add(leftPanel, BorderLayout.WEST);
        navBar.add(rightPanel, BorderLayout.EAST);

        return navBar;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppTheme.BORDER_COLOR));

        // Menu items
        addMenuItem(sidebar, "📊 Thông tin cá nhân", () -> showInfoPanel());
        addMenuItem(sidebar, "📚 Môn học đã đăng ký", () -> showEnrollmentsPanel());
        addMenuItem(sidebar, "🔑 Đổi mật khẩu", () -> showChangePasswordDialog());

        return sidebar;
    }

    private void addMenuItem(JPanel sidebar, String text, Runnable action) {
        JButton menuItem = new JButton(text);
        menuItem.setFont(AppTheme.NORMAL_FONT);
        menuItem.setForeground(AppTheme.TEXT_PRIMARY);
        menuItem.setBackground(Color.WHITE);
        menuItem.setHorizontalAlignment(SwingConstants.LEFT);
        menuItem.setBorderPainted(false);
        menuItem.setFocusPainted(false);
        menuItem.setMaximumSize(new Dimension(250, 50));
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuItem.setBorder(new EmptyBorder(15, 20, 15, 20));

        menuItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                menuItem.setBackground(AppTheme.BACKGROUND_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                menuItem.setBackground(Color.WHITE);
            }
        });

        menuItem.addActionListener(e -> action.run());

        sidebar.add(menuItem);
    }

    private void loadStudentData() {
        SwingWorker<JsonNode, Void> worker = new SwingWorker<>() {

            @Override
            protected JsonNode doInBackground() {
                try {
                    String response = ApiClient.get("/students/dashboard");

                    System.out.println("📥 RAW API RESPONSE:");
                    System.out.println(response);

                    if (response == null || response.isBlank()) {
                        throw new RuntimeException("Response rỗng từ server");
                    }

                    return ApiClient.getObjectMapper().readTree(response);

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Không thể tải dữ liệu: " + e.getMessage(), e);
                }
            }

            @Override
            protected void done() {
                try {
                    studentData = get();
                    System.out.println("✅ ĐÃ PARSE JSON thành công");
                    showInfoPanel();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            StudentDashboardFrame.this,
                            "❌ Lỗi khi tải dữ liệu sinh viên:\n" + ex.getMessage(),
                            "Lỗi API",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }


    private void showInfoPanel() {
        if (studentData == null) {
            return;
        }

        contentPanel.removeAll();

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(AppTheme.BACKGROUND_COLOR);
        infoPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title
        JLabel lblTitle = new JLabel("Thông tin cá nhân");
        lblTitle.setFont(AppTheme.TITLE_FONT);
        lblTitle.setForeground(AppTheme.PRIMARY_COLOR);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(lblTitle);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Info card
        JPanel card = new JPanel();
        card.setLayout(new GridLayout(6, 2, 15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),
                new EmptyBorder(30, 30, 30, 30)
        ));
        card.setMaximumSize(new Dimension(800, 400));
        currentInfoCard = card; // Gán reference cho currentInfoCard

        addInfoRow(card, "Mã sinh viên:", studentData.get("studentCode").asText());
        addInfoRow(card, "Họ và tên:", studentData.get("fullName").asText());
        addInfoRow(card, "Giới tính:", studentData.get("gender").asText());
        addInfoRow(card, "Ngày sinh:", studentData.get("dob").asText());
        addInfoRow(card, "Email:", studentData.get("email").asText());
        addInfoRow(card, "Lớp:", studentData.has("className") ? studentData.get("className").asText() : "Chưa có lớp");

        infoPanel.add(card);
        infoPanel.add(Box.createVerticalGlue());

        contentPanel.add(infoPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(AppTheme.NORMAL_FONT.deriveFont(Font.BOLD));
        lblLabel.setForeground(AppTheme.TEXT_SECONDARY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(AppTheme.NORMAL_FONT);
        lblValue.setForeground(AppTheme.TEXT_PRIMARY);

        panel.add(lblLabel);
        panel.add(lblValue);
    }

    private void showEnrollmentsPanel() {
        if (studentData == null) {
            return;
        }

        contentPanel.removeAll();

        JPanel enrollPanel = new JPanel(new BorderLayout());
        enrollPanel.setBackground(AppTheme.BACKGROUND_COLOR);
        enrollPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title
        JLabel lblTitle = new JLabel("Môn học đã đăng ký");
        lblTitle.setFont(AppTheme.TITLE_FONT);
        lblTitle.setForeground(AppTheme.PRIMARY_COLOR);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitle);

        enrollPanel.add(titlePanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Mã môn", "Tên môn học", "Số tín chỉ", "Học kỳ", "Điểm", "Giảng viên"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JsonNode enrollments = studentData.get("enrollments");
        if (enrollments != null && enrollments.isArray()) {
            for (JsonNode enrollment : enrollments) {
                System.out.println("Keys có trong enrollment:");
                enrollment.fieldNames().forEachRemaining(System.out::println);
                Object[] row = {
                        enrollment.path("subjectId").asText("Chưa có"),
                        enrollment.path("subjectName").asText("Chưa có"),
                        enrollment.path("credits").asInt(0),
                        enrollment.path("semester").asText("Chưa có"),
                        enrollment.path("grade").isNull() ? "Chưa có" : enrollment.path("grade").asDouble(),
                        enrollment.path("lecturerName").asText("Chưa có")
                };
                model.addRow(row);
            }

        }

        JTable table = new JTable(model);
        table.setFont(AppTheme.NORMAL_FONT);
        table.setRowHeight(40);
        table.getTableHeader().setFont(AppTheme.NORMAL_FONT.deriveFont(Font.BOLD));
        table.getTableHeader().setBackground(AppTheme.PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR));

        enrollPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(enrollPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showChangePasswordDialog() {
        ChangePasswordDialog dialog = new ChangePasswordDialog(this);
        dialog.setVisible(true);
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            ApiClient.clearToken();
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }

    private void setupWebSocket() {
        webSocketClient = new WebSocketClient();
        webSocketClient.connect(() -> {
            String currentUsername = ApiClient.getCurrentUsername();
            if (currentUsername != null) {
                System.out.println("🌐 [WEBSOCKET - STUDENT] Bắt đầu kết nối WebSocket");
                System.out.println("   └─ Username: " + currentUsername);

                // ✅ ENABLE Real-time group subscription (đã fix lỗi 403)
                webSocketClient.subscribe("/topic/students/updates", StudentResponse.class, this::handleStudentUpdateRealtime);
                System.out.println("✅ [WEBSOCKET - STUDENT] Đã join vào group real-time");
                System.out.println("   ├─ Topic: /topic/students/updates");
                System.out.println("   └─ Sẽ tự động filter message cho sinh viên: " + currentUsername);

                // Subscribe vào topic môn học đã đăng ký
                webSocketClient.subscribe("/topic/student/" + currentUsername + "/enrollments", StudentDashboardResponse.class, this::handleEnrollmentUpdate);
                System.out.println("✅ [WEBSOCKET - STUDENT] Đã subscribe topic môn học");
                System.out.println("   └─ Topic: /topic/student/" + currentUsername + "/enrollments");
            }
        });
    }

    private void handleStudentUpdateRealtime(StudentResponse updatedStudent) {
        // Chạy trên EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            try {
                String currentUsername = ApiClient.getCurrentUsername();

                System.out.println("📩 [WEBSOCKET - STUDENT] Nhận được broadcast cập nhật sinh viên từ group");
                System.out.println("   ├─ Mã SV trong message: " + updatedStudent.getStudentCode());
                System.out.println("   ├─ Họ tên: " + updatedStudent.getFullName());
                System.out.println("   ├─ Email: " + updatedStudent.getEmail());
                System.out.println("   └─ Username hiện tại: " + currentUsername);

                // 🔥 FILTER: Chỉ xử lý nếu là update của chính mình
                if (updatedStudent.getStudentCode().equals(currentUsername)) {
                    System.out.println("✅ [FILTER] Message này dành cho tôi! Đang cập nhật giao diện...");

                    // Cập nhật studentData
                    String json = ApiClient.getObjectMapper().writeValueAsString(updatedStudent);
                    studentData = ApiClient.getObjectMapper().readTree(json);

                    // 🔥 CẬP NHẬT UI TRỰC TIẾP - KHÔNG CẦN POPUP
                    showInfoPanel();

                    System.out.println("✅ [WEBSOCKET - STUDENT] Đã cập nhật giao diện real-time thành công (silent update)");
                } else {
                    System.out.println("⏭️  [FILTER] Message này không phải của tôi, bỏ qua");
                    System.out.println("   └─ Message dành cho: " + updatedStudent.getStudentCode());
                }
            } catch (Exception ex) {
                System.err.println("❌ [WEBSOCKET - STUDENT] Lỗi khi xử lý cập nhật real-time");
                System.err.println("   └─ Chi tiết: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    private void handleEnrollmentUpdate(StudentDashboardResponse dashboardData) {
        // Chạy trên EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("📩 [WEBSOCKET - STUDENT] Nhận được cập nhật môn học đã đăng ký từ server");

                // Cập nhật studentData với dữ liệu mới
                String json = ApiClient.getObjectMapper().writeValueAsString(dashboardData);
                studentData = ApiClient.getObjectMapper().readTree(json);

                // 🔥 CẬP NHẬT UI TRỰC TIẾP - KHÔNG CẦN POPUP
                showEnrollmentsPanel();

                System.out.println("✅ [WEBSOCKET - STUDENT] Đã cập nhật danh sách môn học real-time thành công (silent update)");
            } catch (Exception ex) {
                System.err.println("❌ [WEBSOCKET - STUDENT] Lỗi khi xử lý cập nhật môn học real-time");
                System.err.println("   └─ Chi tiết: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }
}
