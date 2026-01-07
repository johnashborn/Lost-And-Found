/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author arant
 */

import javax.swing.*;
import java.awt.*;

// Simple Gauge Component - NO JFreeChart needed!
class SimpleGauge extends JPanel {
    private int value;
    private String label;
    private Color color;
    
    public SimpleGauge(int value, String label, Color color) {
        this.value = value;
        this.label = label;
        this.color = color;
        setPreferredSize(new Dimension(85, 85));
        setBackground(Color.WHITE);
    }
    
    public void setValue(int value) {
    this.value = value;
    repaint(); // triggers paintComponent to redraw with new value
    System.out.println("value "+value); //debug sako kadyot
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int centerX = getWidth() / 2;
        int centerY = getHeight() - 20;
        int radius = 30;
        
        // Draw gray background arc
        g2.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawArc(centerX - radius, centerY - radius, radius * 2, radius * 2, 0, 180);
        
        // Draw colored arc based on percentage
        g2.setColor(color);
        int angle = (int) (180 * value / 100.0);
        g2.drawArc(centerX - radius, centerY - radius, radius * 2, radius * 2, 0, angle);
        
        // Draw percentage text
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        String text = value + "%";
        FontMetrics fm = g2.getFontMetrics();
        int textX = centerX - fm.stringWidth(text) / 2;
        g2.drawString(text, textX, centerY - 8);
        
        // Draw label below
        g2.setFont(new Font("Arial", Font.PLAIN, 9));
        fm = g2.getFontMetrics();
        
        // Split label into multiple lines if needed
        String[] words = label.split(" ");
        StringBuilder line1 = new StringBuilder();
        StringBuilder line2 = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            if (i < words.length / 2) {
                line1.append(words[i]).append(" ");
            } else {
                line2.append(words[i]).append(" ");
            }
        }
        
        int y = getHeight() - 12;
        String l1 = line1.toString().trim();
        String l2 = line2.toString().trim();
        
        if (!l1.isEmpty()) {
            g2.drawString(l1, centerX - fm.stringWidth(l1) / 2, y);
        }
        if (!l2.isEmpty()) {
            g2.drawString(l2, centerX - fm.stringWidth(l2) / 2, y + 10);
        }
    }
}