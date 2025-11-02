package iuh.fit.se.gui;

import iuh.fit.se.gui.view.StudentLoginFrame;

import javax.swing.*;

/**
 * Main class để chạy GUI Application cho Sinh viên
 */
public class StudentApp {

    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start GUI on EDT
        SwingUtilities.invokeLater(() -> {
            StudentLoginFrame loginFrame = new StudentLoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
