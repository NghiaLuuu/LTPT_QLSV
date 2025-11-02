package iuh.fit.se.gui;

import iuh.fit.se.gui.view.LoginFrame;

import javax.swing.*;

/**
 * Main class để chạy GUI Application
 */
public class MainGUI {

    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start GUI on EDT
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
