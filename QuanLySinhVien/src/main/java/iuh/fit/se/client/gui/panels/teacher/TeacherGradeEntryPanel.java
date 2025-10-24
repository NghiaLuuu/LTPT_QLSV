package iuh.fit.se.client.gui.panels.teacher;

import iuh.fit.se.client.gui.TeacherMainFrame;
import iuh.fit.se.client.net.NetworkClient;
import iuh.fit.se.common.dto.AuthResponseDTO;

import javax.swing.*;
import java.awt.*;

/**
 * Panel để giảng viên nhập điểm cho sinh viên
 */
public class TeacherGradeEntryPanel extends JPanel implements TeacherMainFrame.RefreshablePanel {
    private NetworkClient networkClient;
    private AuthResponseDTO authResponse;

    public TeacherGradeEntryPanel(NetworkClient networkClient, AuthResponseDTO authResponse) {
        this.networkClient = networkClient;
        this.authResponse = authResponse;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("📝 Nhập điểm cho sinh viên");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTemp = new JLabel("<html><center>Chức năng đang được phát triển...<br><br>" +
            "Vui lòng chọn lớp học phần từ tab 'Lớp học phần của tôi'<br>" +
            "sau đó sử dụng chức năng nhập điểm.</center></html>");
        lblTemp.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTemp.setForeground(Color.GRAY);
        lblTemp.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
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

