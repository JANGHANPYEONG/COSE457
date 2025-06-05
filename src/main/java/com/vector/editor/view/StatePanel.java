package com.vector.editor.view;

import com.vector.editor.model.shape.Shape;
import com.vector.editor.controller.StateManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class StatePanel extends JPanel {
    private final JLabel positionLabel;
    private final JLabel sizeLabel;
    private final StateManager stateManager;
    private Shape currentShape;

    public StatePanel(StateManager stateManager) {
        this.stateManager = stateManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        positionLabel = createEditableLabel("Position: (0, 0)", "Position");
        sizeLabel = createEditableLabel("Size: 0x0", "Size");

        add(positionLabel);
        add(sizeLabel);
        add(Box.createVerticalStrut(10));

        // StateManager의 이벤트 리스닝
        setupStateManagerListeners();

        // 초기 상태 업데이트
        updateShape(stateManager.getCurrentShape());
    }

    private void setupStateManagerListeners() {
        // 현재 선택된 도형 변경 시
        stateManager.addPropertyChangeListener(StateManager.PROPERTY_CURRENT_SHAPE,
            new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    updateShape((Shape) evt.getNewValue());
                }
            });

        // 도형의 위치 변경 시 실시간 업데이트
        stateManager.addPropertyChangeListener(StateManager.PROPERTY_POSITION_CHANGED,
            new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (currentShape != null) {
                        SwingUtilities.invokeLater(() -> {
                            positionLabel.setText(String.format("Position: (%d, %d)",
                                currentShape.getX(), currentShape.getY()));
                        });
                    }
                }
            });

        // 도형의 크기 변경 시 실시간 업데이트
        stateManager.addPropertyChangeListener(StateManager.PROPERTY_SIZE_CHANGED,
            new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (currentShape != null) {
                        SwingUtilities.invokeLater(() -> {
                            sizeLabel.setText(String.format("Size: %dx%d",
                                currentShape.getWidth(), currentShape.getHeight()));
                        });
                    }
                }
            });
    }

    private JLabel createEditableLabel(String initialText, String type) {
        JLabel label = new JLabel(initialText);
        label.setForeground(Color.BLUE);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.setToolTipText("Click to edit " + type.toLowerCase());

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentShape == null) {
                    JOptionPane.showMessageDialog(StatePanel.this,
                        "No shape selected", "Information", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                showEditDialog(type);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (currentShape != null) {
                    label.setForeground(Color.RED);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (currentShape != null) {
                    label.setForeground(Color.BLUE);
                }
            }
        });

        return label;
    }

    private void showEditDialog(String type) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JTextField xField = new JTextField(8);
        JTextField yField = new JTextField(8);

        String title, xLabel, yLabel;
        int currentX, currentY;

        if (type.equals("Position")) {
            title = "Edit Position";
            xLabel = "X:";
            yLabel = "Y:";
            currentX = currentShape.getX();
            currentY = currentShape.getY();
        } else {
            title = "Edit Size";
            xLabel = "Width:";
            yLabel = "Height:";
            currentX = currentShape.getWidth();
            currentY = currentShape.getHeight();
        }

        xField.setText(String.valueOf(currentX));
        yField.setText(String.valueOf(currentY));

        // 레이아웃 설정
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel(xLabel), gbc);

        gbc.gridx = 1;
        panel.add(xField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel(yLabel), gbc);

        gbc.gridx = 1;
        panel.add(yField, gbc);

        // 포커스 설정
        SwingUtilities.invokeLater(() -> {
            xField.requestFocusInWindow();
            xField.selectAll();
        });

        int result = JOptionPane.showConfirmDialog(
            this, panel, title,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                int x = Integer.parseInt(xField.getText().trim());
                int y = Integer.parseInt(yField.getText().trim());

                // 유효성 검사
                if (type.equals("Size") && (x <= 0 || y <= 0)) {
                    JOptionPane.showMessageDialog(this,
                        "Width and height must be positive numbers",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (type.equals("Position") && (x < 0 || y < 0)) {
                    JOptionPane.showMessageDialog(this,
                        "Position coordinates cannot be negative",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // StateManager를 통해 변경 적용 (Command 패턴 사용)
                if (type.equals("Position")) {
                    stateManager.setPosition(x, y);
                } else {
                    stateManager.setSize(x, y);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void updateShape(Shape shape) {
        this.currentShape = shape;

        if (shape == null) {
            positionLabel.setText("Position: -");
            sizeLabel.setText("Size: -");
            positionLabel.setForeground(Color.GRAY);
            sizeLabel.setForeground(Color.GRAY);
            positionLabel.setCursor(Cursor.getDefaultCursor());
            sizeLabel.setCursor(Cursor.getDefaultCursor());
            positionLabel.setToolTipText("No shape selected");
            sizeLabel.setToolTipText("No shape selected");
        } else {
            positionLabel.setText(String.format("Position: (%d, %d)", shape.getX(), shape.getY()));
            sizeLabel.setText(String.format("Size: %dx%d", shape.getWidth(), shape.getHeight()));
            positionLabel.setForeground(Color.BLUE);
            sizeLabel.setForeground(Color.BLUE);
            positionLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            sizeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            positionLabel.setToolTipText("Click to edit position");
            sizeLabel.setToolTipText("Click to edit size");
        }
    }
}