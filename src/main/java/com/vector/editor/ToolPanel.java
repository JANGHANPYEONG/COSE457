package com.vector.editor;

import com.vector.editor.shapes.ImageShape;
import com.vector.editor.tools.FreeDrawTool;
import com.vector.editor.utils.ImageLoader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolPanel extends JPanel {
    private CanvasPanel canvasPanel;
    private static final int BUTTON_SIZE = 50;
    private static final int PANEL_WIDTH = 100;

    private Color strokeColor = Color.BLACK;
    private int strokeWidth = 1;
    
    public ToolPanel(CanvasPanel canvasPanel) {
        this.canvasPanel = canvasPanel;

        setPreferredSize(new Dimension(PANEL_WIDTH, 600));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add shape buttons
        addShapeButton("Rectangle", "R");
        addShapeButton("Ellipse", "E");
        addShapeButton("Line", "L");
        addShapeButton("Text", "T");
        addShapeButton("Free Draw", "F");
        addImageButton();
        addUngroupButton();
    }
    
    private void addShapeButton(String tooltip, String text) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Selected: " + tooltip);

                // TODO: 도형별 Tool 연결
                if (tooltip.equals("Free Draw")) {
                    canvasPanel.setCurrentTool(new FreeDrawTool(canvasPanel, strokeColor, strokeWidth));
                }
            }
        });
        add(button);
        add(Box.createVerticalStrut(5));
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

    private void addUngroupButton() {
        JButton ungroupButton = new JButton("Ungroup");
        ungroupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        ungroupButton.addActionListener(e -> canvasPanel.ungroupSelectedGroup());

        add(ungroupButton);
        add(Box.createVerticalStrut(10));
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
} 