package iuh.fit.se.gui.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.se.dto.response.FacultyResponse;
import iuh.fit.se.gui.component.ModernButton;
import iuh.fit.se.gui.component.ModernTextField;
import iuh.fit.se.gui.util.ApiClient;
import iuh.fit.se.gui.util.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Dialog thêm/sửa Khoa
 */
public class FacultyDialog extends JDialog {

    private ModernTextField txtCode;
    private ModernTextField txtName;
    private JTextArea txtDescription;

    private ModernButton btnSave;
    private ModernButton btnCancel;

    private FacultyResponse faculty;
    private boolean confirmed = false;

    public FacultyDialog(JFrame parent, String title, FacultyResponse faculty) {
        super(parent, title, true);
        this.faculty = faculty;

        initComponents();

        if (faculty != null) {
            loadFacultyData();
        }

        setSize(500, 500);
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
        JLabel lblTitle = new JLabel(faculty == null ? "Thêm Khoa mới" : "Sửa thông tin Khoa");
        lblTitle.setFont(AppTheme.TITLE_FONT);
        lblTitle.setForeground(AppTheme.PRIMARY_COLOR);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(lblTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Code field
        JLabel lblCode = new JLabel("Mã khoa *");
        lblCode.setFont(AppTheme.NORMAL_FONT);
        lblCode.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtCode = new ModernTextField(25);
        txtCode.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));
        txtCode.setEditable(false); // Không cho sửa mã
        txtCode.setBackground(new Color(240, 240, 240)); // Màu xám nhạt

        // Nếu thêm mới, tự động sinh mã
        if (faculty == null) {
            fetchNextFacultyCode();
        }

        mainPanel.add(lblCode);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(txtCode);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Name field
        JLabel lblName = new JLabel("Tên khoa *");
        lblName.setFont(AppTheme.NORMAL_FONT);
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtName = new ModernTextField(25);
        txtName.setMaximumSize(new Dimension(Integer.MAX_VALUE, AppTheme.INPUT_HEIGHT));

        mainPanel.add(lblName);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(txtName);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Description field
        JLabel lblDescription = new JLabel("Mô tả");
        lblDescription.setFont(AppTheme.NORMAL_FONT);
        lblDescription.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtDescription = new JTextArea(5, 25);
        txtDescription.setFont(AppTheme.NORMAL_FONT);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JScrollPane scrollDescription = new JScrollPane(txtDescription);
        scrollDescription.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        scrollDescription.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(lblDescription);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(scrollDescription);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        add(mainPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        btnCancel = new ModernButton("Hủy");
        btnCancel.setBackground(AppTheme.SECONDARY_COLOR);
        btnCancel.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        btnSave = new ModernButton("Lưu");
        btnSave.setBackground(AppTheme.SUCCESS_COLOR);
        btnSave.addActionListener(e -> saveFaculty());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadFacultyData() {
        txtCode.setText(faculty.getCode());
        txtName.setText(faculty.getName());
        if (faculty.getDescription() != null) {
            txtDescription.setText(faculty.getDescription());
        }
    }

    private void saveFaculty() {
        // Validate
        String code = txtCode.getText().trim();
        String name = txtName.getText().trim();
        String description = txtDescription.getText().trim();

        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập mã khoa!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            txtCode.requestFocus();
            return;
        }

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tên khoa!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            txtName.requestFocus();
            return;
        }

        // Prepare request data
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("code", code);
        requestData.put("name", name);
        if (!description.isEmpty()) {
            requestData.put("description", description);
        }

        // Call API
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                ObjectMapper mapper = ApiClient.getObjectMapper();
                String jsonData = mapper.writeValueAsString(requestData);

                if (faculty == null) {
                    // Create new
                    ApiClient.post("/faculties", jsonData);
                } else {
                    // Update
                    ApiClient.put("/faculties/" + faculty.getId(), jsonData);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    confirmed = true;
                    JOptionPane.showMessageDialog(FacultyDialog.this,
                            faculty == null ? "Thêm khoa thành công!" : "Cập nhật khoa thành công!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FacultyDialog.this,
                            "Lỗi: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void fetchNextFacultyCode() {
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                String json = ApiClient.get("/faculties");
                com.fasterxml.jackson.databind.JsonNode root = ApiClient.getObjectMapper().readTree(json);
                int count = root.size();
                return String.format("K%08d", count + 1);
            }

            @Override
            protected void done() {
                try {
                    String code = get();
                    txtCode.setText(code);
                } catch (Exception ex) {
                    txtCode.setText("K00000001");
                }
            }
        };
        worker.execute();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
