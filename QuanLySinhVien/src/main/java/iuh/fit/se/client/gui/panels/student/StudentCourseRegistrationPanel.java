package iuh.fit.se.client.gui.panels.student;

import iuh.fit.se.client.gui.StudentMainFrame;
import iuh.fit.se.client.net.NetworkClient;
import iuh.fit.se.common.dto.AuthResponseDTO;

import javax.swing.*;
import java.awt.*;

/**
 * Panel đăng ký học phần cho sinh viên
 */
public class StudentCourseRegistrationPanel extends JPanel implements StudentMainFrame.RefreshablePanel {
    private NetworkClient networkClient;
    private AuthResponseDTO authResponse;

    public StudentCourseRegistrationPanel(NetworkClient networkClient, AuthResponseDTO authResponse) {
        this.networkClient = networkClient;
        this.authResponse = authResponse;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("📅 Đăng ký học phần");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTemp = new JLabel("<html><center>Chức năng đăng ký học phần đang được phát triển...<br><br>" +
            "Sẽ hiển thị:<br>" +
            "• Danh sách học phần có thể đăng ký<br>" +
            "• Thông tin lịch học, giảng viên<br>" +
            "• Tình trạng còn chỗ<br>" +
            "• Đăng ký/Hủy đăng ký</center></html>");
        lblTemp.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTemp.setForeground(Color.GRAY);
        lblTemp.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(236, 240, 241));
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        contentPanel.add(lblTitle);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(lblTemp);

        centerPanel.add(contentPanel);
        add(centerPanel, BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        // Refresh logic
    }
}

