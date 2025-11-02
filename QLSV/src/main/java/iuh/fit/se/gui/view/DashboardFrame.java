package iuh.fit.se.gui.view;

import iuh.fit.se.gui.component.ModernButton;
import iuh.fit.se.gui.util.ApiClient;
import iuh.fit.se.gui.util.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dashboard chính sau khi đăng nhập
 */
public class DashboardFrame extends JFrame {

    private JPanel contentPanel;
    private JLabel lblWelcome;

    public DashboardFrame() {
        initComponents();
        setTitle("Dashboard - Hệ Thống Quản Lý Sinh Viên");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
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

        // Show home panel by default
        showHomePanel();

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

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ SINH VIÊN");
        lblTitle.setFont(AppTheme.HEADING_FONT);
        lblTitle.setForeground(Color.WHITE);

        leftPanel.add(lblLogo);
        leftPanel.add(lblTitle);

        // User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        lblWelcome = new JLabel("👤 " + ApiClient.getCurrentUsername() + " (" + ApiClient.getCurrentRole() + ")");
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
        addMenuItem(sidebar, "■ Trang chủ", () -> showHomePanel());
        addMenuItem(sidebar, "■■ Quản lý Sinh viên", () -> showStudentPanel());
        addMenuItem(sidebar, "■ Quản lý Lớp học", () -> showClassPanel());
        addMenuItem(sidebar, "■ Quản lý Môn học", () -> showSubjectPanel());
        addMenuItem(sidebar, "■ Quản lý Khoa", () -> showFacultyPanel());

        // Only show lecturer management for ADMIN
        if ("ADMIN".equals(ApiClient.getCurrentRole())) {
            addMenuItem(sidebar, "■■ Quản lý Giảng viên", () -> showLecturerPanel());
        }

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

    private void showHomePanel() {
        contentPanel.removeAll();

        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        homePanel.setBackground(AppTheme.BACKGROUND_COLOR);
        homePanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Welcome message
        JLabel lblTitle = new JLabel("Chào mừng đến với Hệ thống Quản lý Sinh viên!");
        lblTitle.setFont(AppTheme.TITLE_FONT);
        lblTitle.setForeground(AppTheme.PRIMARY_COLOR);

        JLabel lblDesc = new JLabel("Vui lòng chọn chức năng từ menu bên trái");
        lblDesc.setFont(AppTheme.NORMAL_FONT);
        lblDesc.setForeground(AppTheme.TEXT_SECONDARY);

        homePanel.add(lblTitle);
        homePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        homePanel.add(lblDesc);
        homePanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Statistics cards (if ADMIN or LECTURER)
        if ("ADMIN".equals(ApiClient.getCurrentRole()) || "LECTURER".equals(ApiClient.getCurrentRole())) {
            JPanel statsPanel = createStatsPanel();
            homePanel.add(statsPanel);
        }

        contentPanel.add(homePanel, BorderLayout.NORTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 20));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        statsPanel.add(createStatCard("Sinh viên", "0", AppTheme.PRIMARY_COLOR));
        statsPanel.add(createStatCard("Lớp học", "0", AppTheme.SUCCESS_COLOR));
        statsPanel.add(createStatCard("Môn học", "0", AppTheme.WARNING_COLOR));
        statsPanel.add(createStatCard("Đăng ký", "0", AppTheme.DANGER_COLOR));

        return statsPanel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(color);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(AppTheme.NORMAL_FONT);
        lblTitle.setForeground(AppTheme.TEXT_SECONDARY);

        card.add(lblValue);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(lblTitle);

        return card;
    }

    private void showStudentPanel() {
        contentPanel.removeAll();
        StudentPanel studentPanel = new StudentPanel();
        contentPanel.add(studentPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showClassPanel() {
        contentPanel.removeAll();
        ClassPanel classPanel = new ClassPanel();
        contentPanel.add(classPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showSubjectPanel() {
        contentPanel.removeAll();
        SubjectPanel subjectPanel = new SubjectPanel();
        contentPanel.add(subjectPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showFacultyPanel() {
        contentPanel.removeAll();
        FacultyPanel facultyPanel = new FacultyPanel();
        contentPanel.add(facultyPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showEnrollmentPanel() {
        contentPanel.removeAll();
        JLabel label = new JLabel("Quản lý Đăng ký - Đang phát triển...", SwingConstants.CENTER);
        label.setFont(AppTheme.HEADING_FONT);
        contentPanel.add(label, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showUserPanel() {
        contentPanel.removeAll();
        JLabel label = new JLabel("Quản lý Người dùng - Đang phát triển...", SwingConstants.CENTER);
        label.setFont(AppTheme.HEADING_FONT);
        contentPanel.add(label, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showLecturerPanel() {
        contentPanel.removeAll();
        LecturerPanel lecturerPanel = new LecturerPanel();
        contentPanel.add(lecturerPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            ApiClient.logout();
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }
}
