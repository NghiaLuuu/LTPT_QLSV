package iuh.fit.se.gui.component;

import iuh.fit.se.gui.util.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Custom Button với style đẹp
 */
public class ModernButton extends JButton {

    private Color backgroundColor;
    private Color hoverColor;

    public ModernButton(String text) {
        this(text, AppTheme.PRIMARY_COLOR);
    }

    public ModernButton(String text, Color backgroundColor) {
        super(text);
        this.backgroundColor = backgroundColor;
        this.hoverColor = backgroundColor.darker();

        setFont(AppTheme.BUTTON_FONT);
        setForeground(Color.WHITE);
        setBackground(backgroundColor);
        setFocusPainted(false);
        setBorderPainted(false);
        setPreferredSize(new Dimension(150, AppTheme.BUTTON_HEIGHT));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(backgroundColor);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), AppTheme.BORDER_RADIUS, AppTheme.BORDER_RADIUS);

        g2.dispose();
        super.paintComponent(g);
    }
}

