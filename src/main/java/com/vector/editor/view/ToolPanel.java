package com.vector.editor.view;

import com.vector.editor.controller.ToolManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.vector.editor.model.shape.TextShape;
import java.awt.Font;
import java.awt.Color;

public class ToolPanel extends JPanel {
    private ToolManager toolManager;
    private ButtonGroup toolGroup;

    public ToolPanel(ToolManager toolManager) {
        this.toolManager = toolManager;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        toolGroup = new ButtonGroup();
        initializeTools();
    }

    private void initializeTools() {
        addToolButton("select", "Select", "/icons/selection.png");
        addToolButton("rectangle", "Rectangle", "/icons/rectangle.png");
        addToolButton("ellipse", "Ellipse", "/icons/ellipse.png");
        addToolButton("line", "Line", "/icons/line.png");
        addToolButton("text", "Text", "/icons/text.png");
        addToolButton("freedraw", "Free Draw", "/icons/freedraw.png");
        addToolButton("image", "Image", "/icons/image.png");
    }

    private void addToolButton(String toolName, String tooltip, String iconPath) {
        JToggleButton button = new JToggleButton();
        button.setToolTipText(tooltip);

        java.net.URL imgURL = getClass().getResource(iconPath);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            // 아이콘 크기 조정 (32x32)
            Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        } else {
            button.setText(toolName.substring(0, 1).toUpperCase());
        }

        button.setPreferredSize(new Dimension(40, 40));
        button.setMaximumSize(new Dimension(40, 40));
        toolGroup.add(button);
        add(button);

        if (toolName.equals("select")) {
            button.setSelected(true);
        }

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toolManager.setCurrentTool(toolName);
                // Text 도구 선택 시 입력 다이얼로그 표시
                if (toolName.equals("text")) {
                    showTextInputDialog();
                }
            }
        });
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