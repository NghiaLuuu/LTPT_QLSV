package iuh.fit.se.client.gui;

import iuh.fit.se.common.dto.AuthResponseDTO;
import iuh.fit.se.common.dto.LoginDTO;
import iuh.fit.se.common.protocol.Command;
import iuh.fit.se.common.protocol.Request;
import iuh.fit.se.common.protocol.Response;
import iuh.fit.se.common.protocol.Status;
import iuh.fit.se.client.net.NetworkClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Màn hình đăng nhập
 */
public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;
    private NetworkClient networkClient;

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;

    // Modern colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color HEADER_COLOR = new Color(52, 73, 94);
    private static final Color BG_COLOR = new Color(236, 240, 241);

    public LoginFrame() {
        initComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initComponents() {
        setTitle("Đăng nhập - Hệ thống Quản lý Đào tạo");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG_COLOR);
    }

    private void setupLayout() {
        // Main panel with modern design
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        mainPanel.setBackground(Color.WHITE);

        // Logo/Icon panel
        JPanel iconPanel = new JPanel();
        iconPanel.setBackground(Color.WHITE);
        iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel iconLabel = new JLabel("🎓");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        iconPanel.add(iconLabel);

        // Title
        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ ĐÀO TẠO");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setForeground(HEADER_COLOR);

        JLabel lblSubtitle = new JLabel("Vui lòng đăng nhập để tiếp tục");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitle.setForeground(new Color(127, 140, 141));

        // Username panel
        JPanel pnlUsername = new JPanel();
        pnlUsername.setLayout(new BoxLayout(pnlUsername, BoxLayout.Y_AXIS));
        pnlUsername.setMaximumSize(new Dimension(350, 80));
        pnlUsername.setBackground(Color.WHITE);

        JLabel lblUsername = new JLabel("👤 Tên đăng nhập");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsername.setForeground(HEADER_COLOR);
        lblUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtUsername.setMaximumSize(new Dimension(350, 45));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlUsername.add(lblUsername);
        pnlUsername.add(Box.createRigidArea(new Dimension(0, 8)));
        pnlUsername.add(txtUsername);

        // Password panel
        JPanel pnlPassword = new JPanel();
        pnlPassword.setLayout(new BoxLayout(pnlPassword, BoxLayout.Y_AXIS));
        pnlPassword.setMaximumSize(new Dimension(350, 80));
        pnlPassword.setBackground(Color.WHITE);

        JLabel lblPassword = new JLabel("🔒 Mật khẩu");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPassword.setForeground(HEADER_COLOR);
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtPassword.setMaximumSize(new Dimension(350, 45));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlPassword.add(lblPassword);
        pnlPassword.add(Box.createRigidArea(new Dimension(0, 8)));
        pnlPassword.add(txtPassword);

        // Button panel
        JPanel pnlButtons = new JPanel(new GridLayout(1, 2, 15, 0));
        pnlButtons.setMaximumSize(new Dimension(350, 45));
        pnlButtons.setBackground(Color.WHITE);

        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setBackground(PRIMARY_COLOR);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnExit = new JButton("Thoát");
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnExit.setBackground(DANGER_COLOR);
        btnExit.setForeground(Color.WHITE);
        btnExit.setFocusPainted(false);
        btnExit.setBorderPainted(false);
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effects
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btnLogin.isEnabled()) btnLogin.setBackground(PRIMARY_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btnLogin.isEnabled()) btnLogin.setBackground(PRIMARY_COLOR);
            }
        });

        btnExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnExit.setBackground(DANGER_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnExit.setBackground(DANGER_COLOR);
            }
        });

        pnlButtons.add(btnLogin);
        pnlButtons.add(btnExit);

        // Info label
        JLabel lblInfo = new JLabel("<html><center>Mặc định: admin/admin123</center></html>");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInfo.setForeground(new Color(127, 140, 141));
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to main panel
        mainPanel.add(iconPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        mainPanel.add(lblSubtitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 35)));
        mainPanel.add(pnlUsername);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(pnlPassword);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(pnlButtons);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(lblInfo);

        // Wrap in a panel with padding
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(BG_COLOR);
        wrapperPanel.add(mainPanel);

        add(wrapperPanel);
    }

    private void setupEventHandlers() {
        // Login button
        btnLogin.addActionListener(e -> performLogin());

        // Exit button
        btnExit.addActionListener(e -> {
            if (networkClient != null) {
                networkClient.close();
            }
            System.exit(0);
        });

        // Enter key on password field
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

        // Enter key on username field
        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtPassword.requestFocus();
                }
            }
        });
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty()) {
            showModernDialog("Vui lòng nhập tên đăng nhập!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showModernDialog("Vui lòng nhập mật khẩu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        // Show loading
        btnLogin.setEnabled(false);
        btnLogin.setText("⏳ Đang đăng nhập...");

        // Perform login in background thread
        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() throws Exception {
                // Connect to server if not connected
                if (networkClient == null) {
                    try {
                        networkClient = new NetworkClient(SERVER_HOST, SERVER_PORT);
                    } catch (Exception e) {
                        throw new Exception("Không thể kết nối đến server tại " + SERVER_HOST + ":" + SERVER_PORT +
                                          "\n\nVui lòng kiểm tra:\n" +
                                          "1. Server đã được khởi động chưa?\n" +
                                          "2. Server đang chạy trên đúng port " + SERVER_PORT + " không?\n" +
                                          "3. Firewall có chặn kết nối không?\n\n" +
                                          "Chi tiết lỗi: " + e.getMessage());
                    }
                }

                LoginDTO loginDTO = new LoginDTO(username, password);
                Request request = new Request(Command.LOGIN, loginDTO, null);
                return networkClient.sendRequest(request);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();

                    if (response.getStatus() == Status.SUCCESS && response.getData() instanceof AuthResponseDTO) {
                        AuthResponseDTO authResponse = (AuthResponseDTO) response.getData();

                        // Close login frame
                        dispose();

                        // Open appropriate main frame based on role
                        SwingUtilities.invokeLater(() -> {
                            switch (authResponse.getRole()) {
                                case ADMIN:
                                    AdminMainFrame adminFrame = new AdminMainFrame(authResponse, networkClient);
                                    adminFrame.setVisible(true);
                                    break;
                                case GIANG_VIEN:
                                    TeacherMainFrame teacherFrame = new TeacherMainFrame(authResponse, networkClient);
                                    teacherFrame.setVisible(true);
                                    break;
                                case SINH_VIEN:
                                    StudentMainFrame studentFrame = new StudentMainFrame(authResponse, networkClient);
                                    studentFrame.setVisible(true);
                                    break;
                                default:
                                    showModernDialog("Vai trò không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                    } else {
                        showModernDialog(response.getMessage(), "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Đăng nhập");
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                    }
                } catch (Exception ex) {
                    showModernDialog(ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Đăng nhập");
                }
            }
        };

        worker.execute();
    }

    private void showModernDialog(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
