package iuh.fit.se.client.gui.panels.teacher;

import iuh.fit.se.client.gui.TeacherMainFrame;
import iuh.fit.se.client.net.NetworkClient;
import iuh.fit.se.common.dto.AuthResponseDTO;

import javax.swing.*;
import java.awt.*;

/**
 * Panel thống kê cho giảng viên
 */
public class TeacherStatisticsPanel extends JPanel implements TeacherMainFrame.RefreshablePanel {
    private NetworkClient networkClient;
    private AuthResponseDTO authResponse;

    public TeacherStatisticsPanel(NetworkClient networkClient, AuthResponseDTO authResponse) {
        this.networkClient = networkClient;
        this.authResponse = authResponse;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("📊 Thống kê và báo cáo");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTemp = new JLabel("<html><center>Chức năng thống kê đang được phát triển...<br><br>" +
            "Sẽ hiển thị:<br>" +
            "• Thống kê điểm theo lớp<br>" +
            "• Biểu đồ phân bố điểm<br>" +
            "• Tỷ lệ đạt/không đạt</center></html>");
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
