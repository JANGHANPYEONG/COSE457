package com.vector.editor;

import com.vector.editor.command.CommandManager;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;
    
    private CanvasPanel canvasPanel;
    private ToolPanel toolPanel;
    private CommandManager commandManager;
    private ColorPanel colorPanel;
    private StatePanel statePanel;
    
    public MainFrame() {
        setTitle("Vector Graphics Editor");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Initialize components
        commandManager = new CommandManager();
        canvasPanel = new CanvasPanel(commandManager);
        toolPanel = new ToolPanel(canvasPanel, commandManager);
        colorPanel = new ColorPanel();
        statePanel = new StatePanel();
        
        // Add components to frame
        add(toolPanel, BorderLayout.WEST);
        add(canvasPanel, BorderLayout.CENTER);
        add(statePanel, BorderLayout.EAST);
        add(colorPanel, BorderLayout.SOUTH);
        
        // Set up state panel updates
        canvasPanel.setStatePanel(statePanel);
        
        setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
} 