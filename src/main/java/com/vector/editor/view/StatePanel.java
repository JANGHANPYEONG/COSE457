package com.vector.editor.view;

import com.vector.editor.model.shape.Shape;
import com.vector.editor.controller.StateManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StatePanel extends JPanel {
    private final StateManager stateManager;
    private Shape currentShape;

    private final JLabel shapeTypeValue;
    private final JPanel fillColorPreview;
    private final JPanel strokeColorPreview;
    private final JLabel positionValue;
    private final JLabel sizeValue;

    public StatePanel(StateManager stateManager) {
        this.stateManager = stateManager;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("State"));
        setPreferredSize(new Dimension(200, 300));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setBackground(Color.BLACK);

        shapeTypeValue = createValueLabel("-");
        positionValue = createEditableLabel("Position: (0, 0)", "Position");
        sizeValue = createEditableLabel("Size: 0x0", "Size");

        fillColorPreview = createColorBox(true);
        strokeColorPreview = createColorBox(false);

        add(createLabeledBox("Shape Type", shapeTypeValue));
        add(Box.createVerticalStrut(10));
        add(createLabeledBox("Fill Color", null, fillColorPreview));
        add(Box.createVerticalStrut(10));
        add(createLabeledBox("Stroke Color", null, strokeColorPreview));
        add(Box.createVerticalStrut(10));
        add(createLabeledBox("Position", positionValue));
        add(Box.createVerticalStrut(10));
        add(createLabeledBox("Size", sizeValue));

        setupStateManagerListeners();
        updateShape(stateManager.getCurrentShape());
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(50, 100, 255));
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        return label;
    }

    private JPanel createColorBox(boolean isFill) {
        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(150, 30));
        box.setMaximumSize(new Dimension(150, 30));
        box.setBackground(Color.LIGHT_GRAY);
        box.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        box.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        box.setToolTipText("Click to change " + (isFill ? "fill" : "stroke") + " color");

        box.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentShape != null) {
                    Color initial = isFill ? currentShape.getFillColor() : currentShape.getStrokeColor();
                    Color selected = JColorChooser.showDialog(StatePanel.this, "Choose Color", initial);
                    if (selected != null) {
                        if (isFill) {
                            stateManager.setFillColor(selected);
                        } else {
                            stateManager.setStrokeColor(selected);
                        }
                        box.setBackground(selected);
                    }
                }
            }
        });
        return box;
    }

    private JLabel createEditableLabel(String initialText, String type) {
        JLabel label = createValueLabel(initialText);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.setToolTipText("Click to edit " + type.toLowerCase());

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentShape == null) return;
                showEditDialog(type);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (currentShape != null) label.setForeground(Color.RED);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (currentShape != null) label.setForeground(new Color(50, 100, 255));
            }
        });
        return label;
    }

    private JPanel createLabeledBox(String labelText, JLabel valueLabel) {
        return createLabeledBox(labelText, valueLabel, null);
    }

    private JPanel createLabeledBox(String labelText, JLabel valueLabel, JComponent previewBox) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setMaximumSize(new Dimension(180, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setBackground(Color.BLACK);

        JLabel label = new JLabel(labelText);
        label.setForeground(Color.LIGHT_GRAY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);

        if (valueLabel != null) {
            valueLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
            valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(valueLabel);
        }

        if (previewBox != null) {
            previewBox.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(Box.createVerticalStrut(5));
            panel.add(previewBox);
        }

        return panel;
    }

    private void showEditDialog(String type) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField xField = new JTextField();
        JTextField yField = new JTextField();

        if (type.equals("Position")) {
            xField.setText(String.valueOf(currentShape.getX()));
            yField.setText(String.valueOf(currentShape.getY()));
        } else {
            xField.setText(String.valueOf(currentShape.getWidth()));
            yField.setText(String.valueOf(currentShape.getHeight()));
        }

        panel.add(new JLabel(type.equals("Position") ? "X:" : "Width:"));
        panel.add(xField);
        panel.add(new JLabel(type.equals("Position") ? "Y:" : "Height:"));
        panel.add(yField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit " + type, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int x = Integer.parseInt(xField.getText().trim());
                int y = Integer.parseInt(yField.getText().trim());
                if (type.equals("Position")) {
                    stateManager.setPosition(x, y);
                } else {
                    stateManager.setSize(x, y);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setupStateManagerListeners() {
        stateManager.addPropertyChangeListener(StateManager.PROPERTY_CURRENT_SHAPE, evt -> {
            updateShape((Shape) evt.getNewValue());
        });

        stateManager.addPropertyChangeListener(StateManager.PROPERTY_POSITION_CHANGED, evt -> {
            updateShape(currentShape);
        });

        stateManager.addPropertyChangeListener(StateManager.PROPERTY_SIZE_CHANGED, evt -> {
            updateShape(currentShape);
        });
    }

    public void updateShape(Shape shape) {
        this.currentShape = shape;
        if (shape == null) {
            shapeTypeValue.setText("-");
            fillColorPreview.setBackground(Color.LIGHT_GRAY);
            strokeColorPreview.setBackground(Color.LIGHT_GRAY);
            positionValue.setText("(0, 0)");
            sizeValue.setText("0x0");
        } else {
            shapeTypeValue.setText(shape.getClass().getSimpleName().replace("Shape", ""));
            fillColorPreview.setBackground(shape.getFillColor());
            strokeColorPreview.setBackground(shape.getStrokeColor());
            positionValue.setText(String.format("(%d, %d)", shape.getX(), shape.getY()));
            sizeValue.setText(String.format("%dx%d", shape.getWidth(), shape.getHeight()));
        }
    }
}

