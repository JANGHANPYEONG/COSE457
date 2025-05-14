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
    private JPanel colorPreview;
    private Shape currentShape = null;
    private CanvasPanel canvasPanel;
    
    public StatePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, 30));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setBackground(Color.BLACK);

        mousePositionLabel = new JLabel("(0, 0)");
        toolStateLabel = new JLabel("None");
        shapeTypeLabel = new JLabel("None");
        shapePositionLabel = new JLabel("None");
        shapeSizeLabel = new JLabel("None");
        shapeColorLabel = new JLabel("None");
        colorPreview = new JPanel();
        colorPreview.setBackground(Color.LIGHT_GRAY);

        add(createLabeledBox("Mouse", mousePositionLabel, null));
        add(Box.createVerticalStrut(20));
        add(createLabeledBox("Tool", toolStateLabel, null));
        add(Box.createVerticalStrut(20));
        add(createLabeledBox("Shape", shapeTypeLabel, null));
        add(Box.createVerticalStrut(20));
        add(createLabeledBox("Position", shapePositionLabel, null));
        add(Box.createVerticalStrut(20));
        add(createLabeledBox("Size", shapeSizeLabel, null));
        add(Box.createVerticalStrut(20));
        add(createLabeledBox("Color", shapeColorLabel, colorPreview));

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
        mousePositionLabel.setText(String.format("(%d, %d)", x, y));
    }
    
    public void updateToolState(String toolName) {
        toolStateLabel.setText(toolName);
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
            shapeTypeLabel.setText("None");
            shapePositionLabel.setText("None");
            shapeSizeLabel.setText("None");
            shapeColorLabel.setText("None");
            colorPreview.setBackground(Color.LIGHT_GRAY);
            return;
        }
        shapeTypeLabel.setText(shape.getClass().getSimpleName());
        shapePositionLabel.setText(String.format("(%d, %d)", shape.getX(), shape.getY()));
        shapeSizeLabel.setText(String.format("%dx%d", shape.getWidth(), shape.getHeight()));
        shapeColorLabel.setText((shape.getStrokeColor() != null ?
            String.format("#%02x%02x%02x",
                shape.getStrokeColor().getRed(),
                shape.getStrokeColor().getGreen(),
                shape.getStrokeColor().getBlue()) : "None"));

        if (shape.getStrokeColor() != null) {
            colorPreview.setBackground(shape.getStrokeColor());
        }
    }

    private JPanel createLabeledBox(String labelText, JLabel valueLabel, JComponent optionalPreview) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.LIGHT_GRAY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setForeground(new Color(50, 100, 255));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 22));

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.DARK_GRAY);
        box.setMaximumSize(new Dimension(180, 60));
        box.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        box.add(label);
        box.add(valueLabel);

        if (labelText.equals("Color")) {
            optionalPreview.setPreferredSize(new Dimension(170, 50));
            optionalPreview.setAlignmentX(Component.LEFT_ALIGNMENT);
            box.setMaximumSize(new Dimension(180, 100));
            box.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            box.add(Box.createVerticalStrut(5));
            box.add(optionalPreview);
        }

        return box;
    }


    @Override
    public void onShapeChanged(Shape shape) {
        updateShapeInfo(shape);
    }

    public void setCanvasPanel(CanvasPanel canvasPanel) {
        this.canvasPanel = canvasPanel;
    }
} 