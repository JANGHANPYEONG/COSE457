package com.vector.editor;

import com.vector.editor.command.CommandManager;
import com.vector.editor.shapes.ImageShape;
import com.vector.editor.tools.FreeDrawTool;
import com.vector.editor.utils.ImageLoader;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ToolPanel extends JPanel {
    private CanvasPanel canvasPanel;
    private CommandManager commandManager;

    private final Map<String, JButton> toolButtons = new HashMap<>();

    private static final int BUTTON_SIZE = 35;
    private static final int PANEL_WIDTH = 60;

    private Color strokeColor = Color.BLACK;
    private int strokeWidth = 1;
    
    public ToolPanel(CanvasPanel canvasPanel, CommandManager commandManager) {
        this.canvasPanel = canvasPanel;
        this.commandManager = commandManager;

        setPreferredSize(new Dimension(PANEL_WIDTH, 600));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.BLACK);

        // Add tool buttons
        add(Box.createVerticalStrut(5));
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
        String iconPath = "/icons/" + toolId + ".png";
        ImageIcon baseIcon = new ImageIcon(getClass().getResource(iconPath));
        Image scaledImage = baseIcon.getImage().getScaledInstance(BUTTON_SIZE - 10, BUTTON_SIZE - 10, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        ImageIcon grayIcon = recolorIcon(scaledIcon, new Color(100, 100, 100));

        JButton button = new JButton(grayIcon);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setToolTipText(shortcut);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        button.addActionListener(e -> {
            canvasPanel.setCurrentTool(toolId);
            updateToolIcons(toolId);
        });

        toolButtons.put(toolId, button);
        add(button);
        add(Box.createVerticalStrut(15));
    }

    private void addImageButton() {
        String iconPath = "/icons/image.png";
        ImageIcon baseIcon = new ImageIcon(getClass().getResource(iconPath));
        Image scaledImage = baseIcon.getImage().getScaledInstance(BUTTON_SIZE - 10, BUTTON_SIZE - 10, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        ImageIcon grayIcon = recolorIcon(scaledIcon, new Color(100, 100, 100));

        JButton imageButton = new JButton(grayIcon);
        imageButton.setToolTipText("Insert Image");
        imageButton.setBorderPainted(false);
        imageButton.setFocusPainted(false);
        imageButton.setContentAreaFilled(false);

        imageButton.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        imageButton.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        imageButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        imageButton.addActionListener(e -> {
            canvasPanel.setCurrentTool(null); // 도형 도구 선택 해제
            updateToolIcons(null); // 모든 도구 버튼을 회색으로

            imageButton.setIcon(recolorIcon(scaledIcon, new Color(50, 100, 255)));

            // 다시 회색으로 되돌리기 (200ms 후)
            Timer timer = new Timer(200, _ -> imageButton.setIcon(grayIcon));
            timer.setRepeats(false);
            timer.start();

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
        add(Box.createVerticalStrut(15));
    }

    private void addUndoRedoButtons() {
        // Undo button
        String undoIconPath = "/icons/undo.png";
        ImageIcon undoBaseIcon = new ImageIcon(getClass().getResource(undoIconPath));
        Image undoScaledImage = undoBaseIcon.getImage().getScaledInstance(BUTTON_SIZE - 10, BUTTON_SIZE - 10, Image.SCALE_SMOOTH);
        ImageIcon undoScaledIcon = new ImageIcon(undoScaledImage);

        ImageIcon undoGrayIcon = recolorIcon(undoScaledIcon, new Color(100, 100, 100));

        JButton undoButton = new JButton(undoGrayIcon);
        undoButton.setToolTipText("Undo (Ctrl+Z)");
        undoButton.setBorderPainted(false);
        undoButton.setFocusPainted(false);
        undoButton.setContentAreaFilled(false);

        undoButton.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        undoButton.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        undoButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        undoButton.addActionListener(e -> {
            canvasPanel.setCurrentTool(null);
            updateToolIcons(null);

            undoButton.setIcon(recolorIcon(undoScaledIcon, new Color(50, 100, 255)));

            if (commandManager.canUndo()) {
                commandManager.undo();
                canvasPanel.repaint();
            }

            Timer timer = new Timer(200, _ -> undoButton.setIcon(undoGrayIcon));
            timer.setRepeats(false);
            timer.start();
        });

        // Redo button
        String redoIconPath = "/icons/redo.png";
        ImageIcon redoBaseIcon = new ImageIcon(getClass().getResource(redoIconPath));
        Image redoScaledImage = redoBaseIcon.getImage().getScaledInstance(BUTTON_SIZE - 10, BUTTON_SIZE - 10, Image.SCALE_SMOOTH);
        ImageIcon redoScaledIcon = new ImageIcon(redoScaledImage);

        ImageIcon redoGrayIcon = recolorIcon(redoScaledIcon, new Color(100, 100, 100));

        JButton redoButton = new JButton(redoGrayIcon);
        redoButton.setToolTipText("Redo (Ctrl+Y)");
        redoButton.setBorderPainted(false);
        redoButton.setFocusPainted(false);
        redoButton.setContentAreaFilled(false);

        redoButton.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        redoButton.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        redoButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        redoButton.addActionListener(e -> {
            canvasPanel.setCurrentTool(null);
            updateToolIcons(null);

            redoButton.setIcon(recolorIcon(redoScaledIcon, new Color(50, 100, 255)));

            if (commandManager.canRedo()) {
                commandManager.redo();
                canvasPanel.repaint();
            }

            Timer timer = new Timer(200, _ -> redoButton.setIcon(redoGrayIcon));
            timer.setRepeats(false);
            timer.start();
        });

        add(undoButton);
        add(Box.createVerticalStrut(15));
        add(redoButton);

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

    private ImageIcon recolorIcon(ImageIcon originalIcon, Color targetColor) {
        int w = originalIcon.getIconWidth();
        int h = originalIcon.getIconHeight();
        BufferedImage originalImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics g = originalImage.getGraphics();
        g.drawImage(originalIcon.getImage(), 0, 0, null);
        g.dispose();

        BufferedImage coloredImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgba = originalImage.getRGB(x, y);
                int alpha = (rgba >> 24) & 0xff;
                if (alpha == 0) continue; // 투명은 유지
                coloredImage.setRGB(x, y, (targetColor.getRGB() & 0x00ffffff) | (alpha << 24));
            }
        }
        return new ImageIcon(coloredImage);
    }

    public void updateToolIcons(String selectedToolId) {
        for (Map.Entry<String, JButton> entry : toolButtons.entrySet()) {
            String toolId = entry.getKey();
            JButton button = entry.getValue();

            String iconPath = "/icons/" + toolId + ".png";
            ImageIcon baseIcon = new ImageIcon(getClass().getResource(iconPath));
            Image scaledImage = baseIcon.getImage().getScaledInstance(BUTTON_SIZE - 10, BUTTON_SIZE - 10, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            ImageIcon recoloredIcon = recolorIcon(scaledIcon,
                toolId.equals(selectedToolId) ? new Color(50, 100, 255) : new Color(100, 100, 100));

            button.setIcon(recoloredIcon);
        }
    }
}