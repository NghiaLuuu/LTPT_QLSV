package iuh.fit.se.gui.component;

import iuh.fit.se.gui.util.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Custom PasswordField với style đẹp
 */
public class ModernPasswordField extends JPasswordField {

    public ModernPasswordField() {
        this(20);
    }

    public ModernPasswordField(int columns) {
        super(columns);
        setFont(AppTheme.NORMAL_FONT);
        setForeground(AppTheme.TEXT_PRIMARY);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_COLOR, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        setPreferredSize(new Dimension(250, AppTheme.INPUT_HEIGHT));
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

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(AppTheme.BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, AppTheme.BORDER_RADIUS, AppTheme.BORDER_RADIUS);

        g2.dispose();
    }
}
