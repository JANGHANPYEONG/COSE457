package com.vector.editor;

import com.vector.editor.command.CommandManager;
import com.vector.editor.shapes.ImageShape;
import com.vector.editor.tools.FreeDrawTool;
import com.vector.editor.tools.RectangleTool;
import com.vector.editor.tools.LineTool;
import com.vector.editor.tools.TextTool;
import com.vector.editor.utils.ImageLoader;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolPanel extends JPanel {
    private CanvasPanel canvasPanel;
    private CommandManager commandManager;

    private static final int BUTTON_SIZE = 50;
    private static final int PANEL_WIDTH = 100;

    private Color strokeColor = Color.BLACK;
    private int strokeWidth = 1;
    
    public ToolPanel(CanvasPanel canvasPanel, CommandManager commandManager) {
        this.canvasPanel = canvasPanel;
        this.commandManager = commandManager;

        setPreferredSize(new Dimension(PANEL_WIDTH, 600));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add tool buttons
        addToolButton("Selection", "S", "selection");
        addToolButton("Rectangle", "R", "rectangle");
        addToolButton("Ellipse", "E", "ellipse");
        addToolButton("Line", "L", "line");
        addToolButton("Text", "T", "text");
        addToolButton("Free Draw", "F", "freedraw");
        addImageButton();
        addUndoRedoButtons();

        // FreeDrawTool은 ToolManager에 동적으로 등록 필요
        canvasPanel.getToolManager().registerTool("freedraw", new FreeDrawTool(canvasPanel, strokeColor, strokeWidth));
    }
    
    private void addToolButton(String name, String shortcut, String toolId) {
        JButton button = new JButton(name);
        button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setToolTipText(shortcut);
        
        button.addActionListener(e -> {
            canvasPanel.setCurrentTool(toolId);
        });
        
        add(button);
    }

    private void addImageButton() {
        JButton imageButton = new JButton("Img");
        imageButton.setToolTipText("Insert Image");

        imageButton.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        imageButton.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        imageButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        imageButton.addActionListener(e -> {
            Image image = ImageLoader.loadImageFromFile(this);
            if (image != null) {
                // 임의 위치/크기 예시
                int x = 100, y = 100, width = 200, height = 150;
                ImageShape imageShape = new ImageShape(x, y, width, height,
                    null, Color.BLACK, 1, image, true);

                canvasPanel.addShape(imageShape);
                canvasPanel.repaint();
            }
        });

        add(imageButton);
        add(Box.createVerticalStrut(5));
    }

    private void addUndoRedoButtons() {
        // Undo button
        JButton undoButton = new JButton("↩");
        undoButton.setToolTipText("Undo (Ctrl+Z)");

        undoButton.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        undoButton.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        undoButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        undoButton.addActionListener(e -> {
            if (commandManager.canUndo()) {
                commandManager.undo();
                canvasPanel.repaint();
            }
        });

        // Redo button
        JButton redoButton = new JButton("↪");
        redoButton.setToolTipText("Redo (Ctrl+Y)");

        redoButton.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        redoButton.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        redoButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        redoButton.addActionListener(e -> {
            if (commandManager.canRedo()) {
                commandManager.redo();
                canvasPanel.repaint();
            }
        });

        add(undoButton);
        add(Box.createVerticalStrut(5));
        add(redoButton);
        add(Box.createVerticalStrut(5));

        // 키보드 단축키 추가 (Ctrl+Z, Ctrl+Y)
        setupKeyboardShortcuts(undoButton, redoButton);
    }

    private void setupKeyboardShortcuts(JButton undoButton, JButton redoButton) {
        // Undo 단축키 (Ctrl+Z)
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        KeyStroke undoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z,
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        inputMap.put(undoKeyStroke, "undo");
        actionMap.put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undoButton.doClick();
            }
        });

        // Redo 단축키 (Ctrl+Y)
        KeyStroke redoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y,
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        inputMap.put(redoKeyStroke, "redo");
        actionMap.put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redoButton.doClick();
            }
        });
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
} 