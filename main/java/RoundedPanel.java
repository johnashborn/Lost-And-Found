/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author arant
 */
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

public class RoundedPanel extends JPanel {
    private int cornerRadius = 15;

    public RoundedPanel(int radius) {
        super();
        cornerRadius = radius;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Paint rounded background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        
        g2.dispose();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Clip to rounded rectangle
        RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        g2.setClip(roundedRectangle);
        
        super.paint(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getForeground());
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        g2.dispose();
    }
}