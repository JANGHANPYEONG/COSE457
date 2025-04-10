package com.vector.editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class CanvasPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    
    public CanvasPanel() {
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(800, 600));
        
        // Add mouse listeners for drawing
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // TODO: Handle mouse press for drawing
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO: Handle mouse release for drawing
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // TODO: Handle mouse drag for drawing
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);
        
        // TODO: Draw shapes here
    }
} 