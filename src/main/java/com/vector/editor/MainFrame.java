package com.vector.editor;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;
    
    private CanvasPanel canvasPanel;
    private ToolPanel toolPanel;
    
    public MainFrame() {
        setTitle("Vector Graphics Editor");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Initialize components
        canvasPanel = new CanvasPanel();
        toolPanel = new ToolPanel();
        
        // Add components to frame
        add(toolPanel, BorderLayout.WEST);
        add(canvasPanel, BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
} 