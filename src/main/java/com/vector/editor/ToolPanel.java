package com.vector.editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolPanel extends JPanel {
    private static final int BUTTON_SIZE = 50;
    private static final int PANEL_WIDTH = 100;
    
    public ToolPanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, 600));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add shape buttons
        addShapeButton("Rectangle", "R");
        addShapeButton("Ellipse", "E");
        addShapeButton("Line", "L");
        addShapeButton("Text", "T");
        addShapeButton("Free Draw", "F");

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
                // TODO: Handle shape selection
                System.out.println("Selected: " + tooltip);
            }
        });
        add(button);
        add(Box.createVerticalStrut(5));
    }

} 