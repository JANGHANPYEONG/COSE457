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
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
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
        menuBar.setBorder(BorderFactory.createEmptyBorder(0, 65, 0, 0));

        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(new Font("SansSerif", Font.BOLD, 14));
        fileMenu.setForeground(Color.LIGHT_GRAY);
        fileMenu.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        fileMenu.getPopupMenu().setBorder(BorderFactory.createEmptyBorder());

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveItem.setBackground(new Color(50, 50, 50));
        saveItem.setForeground(Color.LIGHT_GRAY);
        saveItem.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        saveItem.addActionListener(e -> handleSave());

        JMenuItem loadItem = new JMenuItem("Load");
        loadItem.setFont(new Font("SansSerif", Font.BOLD, 14));
        loadItem.setBackground(new Color(50, 50, 50));
        loadItem.setForeground(Color.LIGHT_GRAY);
        loadItem.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        loadItem.addActionListener(e -> handleLoad());

        JMenuItem newItem = new JMenuItem("New");
        newItem.setFont(new Font("SansSerif", Font.BOLD, 14));
        newItem.setBackground(new Color(50, 50, 50));
        newItem.setForeground(Color.LIGHT_GRAY);
        newItem.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        newItem.addActionListener(e -> handleNew());

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.add(newItem);

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

    private void handleNew() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Current work will be lost. Continue?",
            "New File",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            canvasPanel.clearAllShapes();
            canvasPanel.repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
} 