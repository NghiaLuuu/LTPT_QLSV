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

    public LoginFrame() {
        initComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initComponents() {
        setTitle("Đăng nhập - Hệ thống Quản lý Đào tạo");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Don't connect here - connect when login button is pressed
    }

    private void setupLayout() {
        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Title
        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ ĐÀO TẠO");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setForeground(new Color(0, 102, 204));

        JLabel lblSubtitle = new JLabel("Đăng nhập hệ thống");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username panel
        JPanel pnlUsername = new JPanel(new BorderLayout(10, 5));
        pnlUsername.setMaximumSize(new Dimension(300, 30));
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setPreferredSize(new Dimension(120, 25));
        txtUsername = new JTextField();
        pnlUsername.add(lblUsername, BorderLayout.WEST);
        pnlUsername.add(txtUsername, BorderLayout.CENTER);

        // Password panel
        JPanel pnlPassword = new JPanel(new BorderLayout(10, 5));
        pnlPassword.setMaximumSize(new Dimension(300, 30));
        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setPreferredSize(new Dimension(120, 25));
        txtPassword = new JPasswordField();
        pnlPassword.add(lblPassword, BorderLayout.WEST);
        pnlPassword.add(txtPassword, BorderLayout.CENTER);

        // Button panel
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setPreferredSize(new Dimension(120, 35));
        btnLogin.setBackground(new Color(0, 102, 204));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);

        btnExit = new JButton("Thoát");
        btnExit.setPreferredSize(new Dimension(120, 35));
        btnExit.setBackground(new Color(220, 53, 69));
        btnExit.setForeground(Color.WHITE);
        btnExit.setFocusPainted(false);

        pnlButtons.add(btnLogin);
        pnlButtons.add(btnExit);

        // Add components to main panel
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(lblSubtitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(pnlUsername);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(pnlPassword);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(pnlButtons);

        add(mainPanel);
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
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập tên đăng nhập!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập mật khẩu!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        // Show loading
        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");

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

                        // Open main frame
                        SwingUtilities.invokeLater(() -> {
                            AdminMainFrame mainFrame = new AdminMainFrame(authResponse, networkClient);
                            mainFrame.setVisible(true);
                        });
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this,
                            response.getMessage(),
                            "Đăng nhập thất bại",
                            JOptionPane.ERROR_MESSAGE);
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                        "Lỗi: " + ex.getMessage(),
                        "Lỗi kết nối",
                        JOptionPane.ERROR_MESSAGE);

                    // Close failed connection
                    if (networkClient != null) {
                        networkClient.close();
                        networkClient = null;
                    }
                } finally {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Đăng nhập");
                }
            }
        };

        worker.execute();
    }

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
