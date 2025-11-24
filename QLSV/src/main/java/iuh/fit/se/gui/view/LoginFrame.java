package iuh.fit.se.gui.view;

import iuh.fit.se.dto.response.JwtResponse;
import iuh.fit.se.gui.component.ModernButton;
import iuh.fit.se.gui.component.ModernPasswordField;
import iuh.fit.se.gui.component.ModernTextField;
import iuh.fit.se.gui.util.ApiClient;
import iuh.fit.se.gui.util.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Màn hình đăng nhập
 */
public class LoginFrame extends JFrame {

    private ModernTextField txtUsername;
    private ModernPasswordField txtPassword;
    private ModernButton btnLogin;
    private JLabel lblTitle;
    private JLabel lblError;

    public LoginFrame() {
        initComponents();
        setTitle("Đăng Nhập - Hệ Thống Quản Lý Sinh Viên");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initComponents() {
        // Main panel với gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, AppTheme.PRIMARY_COLOR, 0, h, AppTheme.PRIMARY_DARK);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        // Card panel (login box)
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Logo/Icon
        JLabel lblLogo = new JLabel("🎓");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        lblTitle = new JLabel("ĐĂNG NHẬP");
        lblTitle.setFont(AppTheme.TITLE_FONT);
        lblTitle.setForeground(AppTheme.PRIMARY_COLOR);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel lblSubtitle = new JLabel("Hệ Thống Quản Lý Sinh Viên");
        lblSubtitle.setFont(AppTheme.NORMAL_FONT);
        lblSubtitle.setForeground(AppTheme.TEXT_SECONDARY);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Error label
        lblError = new JLabel(" ");
        lblError.setFont(AppTheme.SMALL_FONT);
        lblError.setForeground(AppTheme.DANGER_COLOR);
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username field
        JLabel lblUsername = new JLabel("Tên đăng nhập");
        lblUsername.setFont(AppTheme.NORMAL_FONT);
        lblUsername.setForeground(AppTheme.TEXT_PRIMARY);
        lblUsername.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtUsername = new ModernTextField(20);
        txtUsername.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtUsername.setMaximumSize(new Dimension(300, AppTheme.INPUT_HEIGHT));

        // Password field
        JLabel lblPassword = new JLabel("Mật khẩu");
        lblPassword.setFont(AppTheme.NORMAL_FONT);
        lblPassword.setForeground(AppTheme.TEXT_PRIMARY);
        lblPassword.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtPassword = new ModernPasswordField(20);
        txtPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtPassword.setMaximumSize(new Dimension(300, AppTheme.INPUT_HEIGHT));

        // Login button
        btnLogin = new ModernButton("ĐĂNG NHẬP");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(300, AppTheme.BUTTON_HEIGHT));
        btnLogin.addActionListener(e -> handleLogin());

        // Enter key listener
        txtPassword.addActionListener(e -> handleLogin());

        // Add components to card panel
        cardPanel.add(lblLogo);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        cardPanel.add(lblTitle);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        cardPanel.add(lblSubtitle);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        cardPanel.add(lblError);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        cardPanel.add(lblUsername);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        cardPanel.add(txtUsername);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        cardPanel.add(lblPassword);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        cardPanel.add(txtPassword);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        cardPanel.add(btnLogin);

        // Add shadow effect
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1),
                        BorderFactory.createEmptyBorder(30, 30, 30, 30)
                )
        ));

        mainPanel.add(cardPanel);
        add(mainPanel);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // Show loading
        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");
        lblError.setText(" ");

        // Call API in background thread
        SwingWorker<JwtResponse, Void> worker = new SwingWorker<>() {
            @Override
            protected JwtResponse doInBackground() throws Exception {
                return ApiClient.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    JwtResponse response = get();
                    // Login success
                    dispose();
                    SwingUtilities.invokeLater(() -> {
                        // Check role and redirect accordingly
                        if ("STUDENT".equals(response.getRole())) {
                            StudentDashboardFrame dashboard = new StudentDashboardFrame();
                            dashboard.setVisible(true);
                        } else {
                            DashboardFrame dashboard = new DashboardFrame();
                            dashboard.setVisible(true);
                        }
                    });
                } catch (Exception ex) {
                    // Hiển thị thông báo lỗi chi tiết từ backend
                    String errorMessage = ex.getMessage();
                    if (errorMessage != null && !errorMessage.isEmpty()) {
                        showError(errorMessage);
                    } else {
                        showError("Đăng nhập thất bại! Vui lòng kiểm tra lại thông tin.");
                    }
                    btnLogin.setEnabled(true);
                    btnLogin.setText("ĐĂNG NHẬP");
                }
            }
        };
        worker.execute();
    }

    private void showError(String message) {
        lblError.setText(message);
    }
}
