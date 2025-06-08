package com.vector.editor.view;

import com.vector.editor.controller.ToolManager;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import java.awt.*;
import com.vector.editor.model.shape.TextShape;
import java.awt.Font;
import java.awt.Color;

public class ToolPanel extends JPanel {
    private ToolManager toolManager;
    private final Map<String, JButton> toolButtons = new HashMap<>();

    private static final int BUTTON_SIZE = 40;

    public ToolPanel(ToolManager toolManager) {
        this.toolManager = toolManager;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setBackground(Color.BLACK);

        initializeTools();
    }

    private void initializeTools() {
        addToolButton("select", "Select", "/icons/select.png");
        addToolButton("rectangle", "Rectangle", "/icons/rectangle.png");
        addToolButton("ellipse", "Ellipse", "/icons/ellipse.png");
        addToolButton("line", "Line", "/icons/line.png");
        addToolButton("text", "Text", "/icons/text.png");
        addToolButton("freedraw", "Free Draw", "/icons/freedraw.png");
        addToolButton("image", "Image", "/icons/image.png");

        updateToolIcons("select");
    }

    private void addToolButton(String toolName, String tooltip, String iconPath) {
        ImageIcon baseIcon = new ImageIcon(getClass().getResource(iconPath));
        Image scaledImage = baseIcon.getImage().getScaledInstance(BUTTON_SIZE - 10, BUTTON_SIZE - 10, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        ImageIcon grayIcon = recolorIcon(scaledIcon, new Color(100, 100, 100));
        JButton button = new JButton(grayIcon);

        button.setToolTipText(tooltip);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addActionListener(e -> {
            toolManager.setCurrentTool(toolName);
            updateToolIcons(toolName);

            if (toolName.equals("text")) {
                showTextInputDialog();
            }
        });

        toolButtons.put(toolName, button);
        add(button);
        add(Box.createVerticalStrut(15));
    }

    private void updateToolIcons(String selectedTool) {
        for (Map.Entry<String, JButton> entry : toolButtons.entrySet()) {
            String tool = entry.getKey();
            JButton button = entry.getValue();

            String iconPath = "/icons/" + tool + ".png";

            ImageIcon baseIcon = new ImageIcon(getClass().getResource(iconPath));
            Image scaledImage = baseIcon.getImage().getScaledInstance(BUTTON_SIZE - 8, BUTTON_SIZE - 8, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            Color color = tool.equals(selectedTool) ? new Color(50, 100, 255) : new Color(100, 100, 100);
            button.setIcon(recolorIcon(scaledIcon, color));
        }
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
                if (alpha == 0) continue;
                coloredImage.setRGB(x, y, (targetColor.getRGB() & 0x00ffffff) | (alpha << 24));
            }
        }
        return new ImageIcon(coloredImage);
    }

    // 텍스트 입력 다이얼로그
    private void showTextInputDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2));
        JTextField textField = new JTextField();
        JComboBox<String> fontBox = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        JSpinner fontSizeSpinner = new JSpinner(new SpinnerNumberModel(18, 8, 100, 1));
        JButton colorButton = new JButton();
        colorButton.setBackground(Color.BLACK);
        colorButton.addActionListener(e -> {
            Color c = JColorChooser.showDialog(panel, "텍스트 색상 선택", colorButton.getBackground());
            if (c != null) colorButton.setBackground(c);
        });
        panel.add(new JLabel("텍스트:")); panel.add(textField);
        panel.add(new JLabel("폰트:")); panel.add(fontBox);
        panel.add(new JLabel("크기:")); panel.add(fontSizeSpinner);
        panel.add(new JLabel("색상:")); panel.add(colorButton);
        int result = JOptionPane.showConfirmDialog(this, panel, "텍스트 입력", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String text = textField.getText();
            String fontName = (String) fontBox.getSelectedItem();
            int fontSize = (Integer) fontSizeSpinner.getValue();
            Color color = colorButton.getBackground();
            Font font = new Font(fontName, Font.PLAIN, fontSize);
            // 중앙에 텍스트 추가 (Document는 ToolManager에서 접근)
            if (toolManager != null && toolManager.getCurrentTool() != null) {
                // Document를 ToolManager에서 가져오거나, MainFrame에서 전달 필요
                // 임시로 ToolManager에 getDocument()가 있다고 가정
                try {
                    java.lang.reflect.Method m = toolManager.getClass().getDeclaredMethod("getDocument");
                    m.setAccessible(true);
                    com.vector.editor.model.Document doc = (com.vector.editor.model.Document) m.invoke(toolManager);
                    TextShape shape = new TextShape(200, 200, text, font, color);
                    doc.addShape(shape);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
} 