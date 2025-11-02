package iuh.fit.se.gui.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.se.gui.component.ModernButton;
import iuh.fit.se.gui.component.ModernPasswordField;
import iuh.fit.se.gui.util.ApiClient;
import iuh.fit.se.gui.util.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Dialog đổi mật khẩu
 */
public class ChangePasswordDialog extends JDialog {

    private ModernPasswordField txtOldPassword;
    private ModernPasswordField txtNewPassword;
    private ModernPasswordField txtConfirmPassword;
    private ModernButton btnSave;
    private ModernButton btnCancel;

    public ChangePasswordDialog(JFrame parent) {
        super(parent, "Đổi mật khẩu", true);
        initComponents();
        setSize(450, 400);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title
        JLabel lblTitle = new JLabel("Đổi mật khẩu");
        lblTitle.setFont(AppTheme.TITLE_FONT);
        lblTitle.setForeground(AppTheme.PRIMARY_COLOR);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(lblTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Old password field
        JLabel lblOldPassword = new JLabel("Mật khẩu cũ *");
        lblOldPassword.setFont(AppTheme.NORMAL_FONT);
        lblOldPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtOldPassword = new ModernPasswordField(25);
        txtOldPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));

        mainPanel.add(lblOldPassword);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(txtOldPassword);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // New password field
        JLabel lblNewPassword = new JLabel("Mật khẩu mới *");
        lblNewPassword.setFont(AppTheme.NORMAL_FONT);
        lblNewPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtNewPassword = new ModernPasswordField(25);
        txtNewPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));

        mainPanel.add(lblNewPassword);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(txtNewPassword);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Confirm password field
        JLabel lblConfirmPassword = new JLabel("Xác nhận mật khẩu mới *");
        lblConfirmPassword.setFont(AppTheme.NORMAL_FONT);
        lblConfirmPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtConfirmPassword = new ModernPasswordField(25);
        txtConfirmPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));

        mainPanel.add(lblConfirmPassword);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(txtConfirmPassword);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        add(mainPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        btnCancel = new ModernButton("Hủy");
        btnCancel.setBackground(AppTheme.SECONDARY_COLOR);
        btnCancel.addActionListener(e -> dispose());

        btnSave = new ModernButton("Đổi mật khẩu");
        btnSave.setBackground(AppTheme.SUCCESS_COLOR);
        btnSave.addActionListener(e -> changePassword());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void changePassword() {
        // Validate
        String oldPassword = new String(txtOldPassword.getPassword());
        String newPassword = new String(txtNewPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        if (oldPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập mật khẩu cũ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            txtOldPassword.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập mật khẩu mới!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            txtNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 8) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu mới phải có ít nhất 8 ký tự!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            txtNewPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu mới và xác nhận mật khẩu không khớp!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            txtConfirmPassword.requestFocus();
            return;
        }

        // Prepare request data
        Map<String, String> requestData = new HashMap<>();
        requestData.put("oldPassword", oldPassword);
        requestData.put("newPassword", newPassword);

        // Call API
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                ObjectMapper mapper = ApiClient.getObjectMapper();
                String jsonData = mapper.writeValueAsString(requestData);
                ApiClient.post("/auth/change-password", jsonData);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(ChangePasswordDialog.this,
                            "Đổi mật khẩu thành công!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ChangePasswordDialog.this,
                            "Lỗi: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}

