package com.vector.editor;

import com.vector.editor.command.CommandManager;
import com.vector.editor.core.Shape;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.*;
import java.awt.*;
import java.util.List;

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

        setJMenuBar(createMenuBar());

        // Add components to frame
        add(toolPanel, BorderLayout.WEST);
        add(canvasPanel, BorderLayout.CENTER);
        add(statePanel, BorderLayout.EAST);
        add(colorPanel, BorderLayout.SOUTH);
        
        // Set up state panel updates
        canvasPanel.setStatePanel(statePanel);
        
        setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.BLACK);
        menuBar.setBorder(BorderFactory.createEmptyBorder());

        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.LIGHT_GRAY);
        fileMenu.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        fileMenu.getPopupMenu().setBorder(BorderFactory.createEmptyBorder());

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setBackground(Color.BLACK);
        saveItem.setForeground(Color.LIGHT_GRAY);
        saveItem.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        saveItem.addActionListener(e -> handleSave());

        JMenuItem loadItem = new JMenuItem("Load");
        loadItem.setBackground(Color.BLACK);
        loadItem.setForeground(Color.LIGHT_GRAY);
        loadItem.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        loadItem.addActionListener(e -> handleLoad());

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);

        menuBar.add(fileMenu);

        return menuBar;
    }

    private void handleSave() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(canvasPanel.getShapes());
                JOptionPane.showMessageDialog(this, "Saved successfully.");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to save.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void handleLoad() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                List<Shape> loadedShapes = (List<Shape>) in.readObject();
                canvasPanel.setShapes(loadedShapes);
                JOptionPane.showMessageDialog(this, "Loaded successfully.");
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
} 