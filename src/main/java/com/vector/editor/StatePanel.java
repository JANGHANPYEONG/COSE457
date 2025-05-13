package com.vector.editor;

import com.vector.editor.core.Shape;
import com.vector.editor.core.ShapeObserver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;

public class StatePanel extends JPanel implements ShapeObserver {
    private JLabel mousePositionLabel;
    private JLabel toolStateLabel;
    private JLabel shapeTypeLabel;
    private JLabel shapePositionLabel;
    private JLabel shapeSizeLabel;
    private JLabel shapeColorLabel;
    private Shape currentShape = null;
    private CanvasPanel canvasPanel;
    
    public StatePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, 30));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setBackground(Color.BLACK);
        
        mousePositionLabel = new JLabel("Mouse: (0, 0)");
        toolStateLabel = new JLabel("Tool: None");
        shapeTypeLabel = new JLabel("Shape: None");
        shapePositionLabel = new JLabel("Position: None");
        shapeSizeLabel = new JLabel("Size: None");
        shapeColorLabel = new JLabel("Color: None");
        
        add(mousePositionLabel);
        add(Box.createVerticalStrut(20));
        add(toolStateLabel);
        add(Box.createVerticalStrut(20));
        add(shapeTypeLabel);
        add(Box.createVerticalStrut(20));
        add(shapePositionLabel);
        add(Box.createVerticalStrut(20));
        add(shapeSizeLabel);
        add(Box.createVerticalStrut(20));
        add(shapeColorLabel);

        shapePositionLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentShape == null) return;
                String input = JOptionPane.showInputDialog(StatePanel.this, "Enter new position (x, y):", String.format("%d, %d", currentShape.getX(), currentShape.getY()));
                if (input != null && input.matches("\\s*\\d+\\s*,\\s*\\d+\\s*")) {
                    String[] parts = input.split(",");
                    int x = Integer.parseInt(parts[0].trim());
                    int y = Integer.parseInt(parts[1].trim());
                    currentShape.setPosition(x, y);
                    if (canvasPanel != null) {
                        canvasPanel.onShapeChanged(currentShape);
                        canvasPanel.repaint();
                    }
                }
            }
        });
        shapeSizeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentShape == null) return;
                String input = JOptionPane.showInputDialog(StatePanel.this, "Enter new size (width, height):", String.format("%d, %d", currentShape.getWidth(), currentShape.getHeight()));
                if (input != null && input.matches("\\s*\\d+\\s*,\\s*\\d+\\s*")) {
                    String[] parts = input.split(",");
                    int w = Integer.parseInt(parts[0].trim());
                    int h = Integer.parseInt(parts[1].trim());
                    currentShape.setSize(w, h);
                    if (canvasPanel != null) {
                        canvasPanel.onShapeChanged(currentShape);
                        canvasPanel.repaint();
                    }
                }
            }
        });
    }
    
    public void updateMousePosition(int x, int y) {
        mousePositionLabel.setText(String.format("Mouse: (%d, %d)", x, y));
    }
    
    public void updateToolState(String toolName) {
        toolStateLabel.setText("Tool: " + toolName);
    }
    
    public void updateShapeInfo(Shape shape) {
        if (currentShape != null) {
            currentShape.removeObserver(this);
        }
        currentShape = shape;
        if (shape != null) {
            shape.addObserver(this);
        }
        if (shape == null) {
            shapeTypeLabel.setText("Shape: None");
            shapePositionLabel.setText("Position: None");
            shapeSizeLabel.setText("Size: None");
            shapeColorLabel.setText("Color: None");
            return;
        }
        shapeTypeLabel.setText(String.format("Shape: %s", shape.getClass().getSimpleName()));
        shapePositionLabel.setText(String.format("Position: (%d, %d)", shape.getX(), shape.getY()));
        shapeSizeLabel.setText(String.format("Size: %dx%d", shape.getWidth(), shape.getHeight()));
        shapeColorLabel.setText("Color: " + (shape.getStrokeColor() != null ?
            String.format("#%02x%02x%02x",
                shape.getStrokeColor().getRed(),
                shape.getStrokeColor().getGreen(),
                shape.getStrokeColor().getBlue()) : "None"));
    }

    @Override
    public void onShapeChanged(Shape shape) {
        updateShapeInfo(shape);
    }

    public void setCanvasPanel(CanvasPanel canvasPanel) {
        this.canvasPanel = canvasPanel;
    }
} 