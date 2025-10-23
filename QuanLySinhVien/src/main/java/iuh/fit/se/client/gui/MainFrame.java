package iuh.fit.se.client.gui;

import iuh.fit.se.client.net.NetworkClient;
import iuh.fit.se.common.model.Gender;
import iuh.fit.se.common.model.SinhVienDTO;
import iuh.fit.se.common.protocol.Command;
import iuh.fit.se.common.protocol.Request;
import iuh.fit.se.common.protocol.Response;
import iuh.fit.se.common.protocol.Status;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainFrame extends JFrame {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;

    private NetworkClient networkClient;
    private JTable tblStudents;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField txtSearch;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh;
    private JLabel lblStatus;

    public MainFrame() {
        super("Chương trình Quản lý Sinh viên");

        // Kết nối đến server
        try {
            networkClient = new NetworkClient(SERVER_HOST, SERVER_PORT);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không thể kết nối đến server!\n" + e.getMessage(),
                    "Lỗi kết nối",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        initComponents();
        setupLayout();
        setupListeners();

        // Tải dữ liệu ban đầu
        loadAllStudents();
    }

    private void initComponents() {
        // Panel tìm kiếm (NORTH)
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlSearch.add(new JLabel("Tìm kiếm (theo tên hoặc mã SV):"));
        txtSearch = new JTextField(30);
        pnlSearch.add(txtSearch);

        // Bảng dữ liệu (CENTER)
        String[] columns = {"Mã SV", "Họ Tên", "Ngày Sinh", "Giới Tính", "Chuyên Ngành", "Lớp", "Điểm TB"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp
            }
        };

        tblStudents = new JTable(tableModel);
        tblStudents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblStudents.getTableHeader().setReorderingAllowed(false);
        tblStudents.setRowHeight(25);

        // Setup sorter cho tìm kiếm
        sorter = new TableRowSorter<>(tableModel);
        tblStudents.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(tblStudents);

        // Panel chức năng (SOUTH)
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdd = new JButton("Thêm Sinh viên");
        btnEdit = new JButton("Sửa Sinh viên");
        btnDelete = new JButton("Xóa Sinh viên");
        btnRefresh = new JButton("Làm mới Danh sách");

        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

        pnlButtons.add(btnAdd);
        pnlButtons.add(btnEdit);
        pnlButtons.add(btnDelete);
        pnlButtons.add(btnRefresh);

        // Status bar
        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblStatus = new JLabel("Sẵn sàng");
        pnlStatus.add(lblStatus);

        // Layout chính
        setLayout(new BorderLayout(5, 5));
        add(pnlSearch, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(pnlButtons, BorderLayout.CENTER);
        southPanel.add(pnlStatus, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void setupLayout() {
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setupListeners() {
        // Listener cho selection trong table
        tblStudents.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = tblStudents.getSelectedRow() != -1;
                btnEdit.setEnabled(hasSelection);
                btnDelete.setEnabled(hasSelection);
            }
        });

        // Listener cho tìm kiếm (live search)
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
        });

        // Button listeners
        btnAdd.addActionListener(e -> addStudent());
        btnEdit.addActionListener(e -> editStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnRefresh.addActionListener(e -> loadAllStudents());
    }

    private void filterTable() {
        String text = txtSearch.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void loadAllStudents() {
        lblStatus.setText("Đang tải dữ liệu...");

        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() throws Exception {
                Request request = new Request(Command.GET_ALL_STUDENTS, null);
                return networkClient.sendRequest(request);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.getStatus() == Status.SUCCESS) {
                        @SuppressWarnings("unchecked")
                        List<SinhVienDTO> students = (List<SinhVienDTO>) response.getData();
                        loadDataToTable(students);
                        lblStatus.setText("Đã tải " + students.size() + " sinh viên");
                    } else {
                        lblStatus.setText("Lỗi: " + response.getMessage());
                        JOptionPane.showMessageDialog(MainFrame.this,
                                response.getMessage(),
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    lblStatus.setText("Mất kết nối đến server");
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Mất kết nối đến server: " + e.getMessage(),
                            "Lỗi kết nối",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void loadDataToTable(List<SinhVienDTO> students) {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (SinhVienDTO sv : students) {
            Object[] row = {
                sv.getMaSV(),
                sv.getHoTen(),
                sv.getNgaySinh() != null ? sv.getNgaySinh().format(formatter) : "",
                sv.getGioiTinh(),
                sv.getChuyenNganh(),
                sv.getLop(),
                String.format("%.2f", sv.getDiemTB())
            };
            tableModel.addRow(row);
        }
    }

    private void addStudent() {
        StudentDialog dialog = new StudentDialog(this, networkClient, null);
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            loadAllStudents();
        }
    }

    private void editStudent() {
        int selectedRow = tblStudents.getSelectedRow();
        if (selectedRow == -1) return;

        String maSV = (String) tableModel.getValueAt(tblStudents.convertRowIndexToModel(selectedRow), 0);

        lblStatus.setText("Đang tải thông tin sinh viên...");

        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() throws Exception {
                Request request = new Request(Command.FIND_STUDENT_BY_ID, maSV);
                return networkClient.sendRequest(request);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.getStatus() == Status.SUCCESS) {
                        SinhVienDTO student = (SinhVienDTO) response.getData();
                        StudentDialog dialog = new StudentDialog(MainFrame.this, networkClient, student);
                        dialog.setVisible(true);

                        if (dialog.isSuccess()) {
                            loadAllStudents();
                        }
                        lblStatus.setText("Sẵn sàng");
                    } else {
                        lblStatus.setText("Lỗi");
                        JOptionPane.showMessageDialog(MainFrame.this,
                                response.getMessage(),
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    lblStatus.setText("Lỗi");
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Lỗi khi tải thông tin: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void deleteStudent() {
        int selectedRow = tblStudents.getSelectedRow();
        if (selectedRow == -1) return;

        int modelRow = tblStudents.convertRowIndexToModel(selectedRow);
        String maSV = (String) tableModel.getValueAt(modelRow, 0);
        String hoTen = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa sinh viên:\n" + hoTen + " (" + maSV + ")?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        lblStatus.setText("Đang xóa...");

        SwingWorker<Response, Void> worker = new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() throws Exception {
                Request request = new Request(Command.DELETE_STUDENT, maSV);
                return networkClient.sendRequest(request);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.getStatus() == Status.SUCCESS) {
                        lblStatus.setText("Đã xóa thành công");
                        JOptionPane.showMessageDialog(MainFrame.this,
                                "Xóa sinh viên thành công!",
                                "Thành công",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadAllStudents();
                    } else {
                        lblStatus.setText("Lỗi");
                        JOptionPane.showMessageDialog(MainFrame.this,
                                response.getMessage(),
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    lblStatus.setText("Lỗi");
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Lỗi khi xóa: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    public static void main(String[] args) {
        // Set Look and Feel sang Nimbus (hiện đại hơn Metal mặc định)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Nếu không set được Nimbus, dùng mặc định
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

