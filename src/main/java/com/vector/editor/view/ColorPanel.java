package com.vector.editor.view;

import com.vector.editor.model.Document;
import javax.swing.*;
import java.awt.*;

public class ColorPanel extends JPanel {
    private Document document;
    private JButton fillColorButton;
    private JButton strokeColorButton;
    private JSpinner strokeWidthSpinner;

    public ColorPanel(Document document) {
        this.document = document;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setBackground(Color.BLACK);
        setMaximumSize(new Dimension(90, Integer.MAX_VALUE));
        setAlignmentX(Component.LEFT_ALIGNMENT);


        setupUI();
    }

    private void setupUI() {
        // 채우기 색상
        JPanel fillColorPanel = new JPanel();
        fillColorPanel.setLayout(new BoxLayout(fillColorPanel, BoxLayout.Y_AXIS));
        fillColorPanel.setMaximumSize(new Dimension(120, 80));
        fillColorPanel.setBackground(Color.BLACK);
        fillColorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fillColorPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel fillColorLabel = new JLabel("Fill Color");
        fillColorLabel.setForeground(Color.LIGHT_GRAY);
        fillColorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fillColorPanel.add(fillColorLabel);
        fillColorPanel.add(Box.createVerticalStrut(5));

        fillColorButton = new JButton();
        fillColorButton.setBackground(document.getFillColor());
        fillColorButton.setPreferredSize(new Dimension(120, 30));
        fillColorButton.setMaximumSize(new Dimension(120, 30));
        fillColorButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        fillColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Fill Color", 
                document.getFillColor());
            if (newColor != null) {
                document.setFillColor(newColor);
                fillColorButton.setBackground(newColor);
            }
        });
        fillColorPanel.add(fillColorButton);
        add(fillColorPanel);
        
        // 선 색상
        JPanel strokeColorPanel = new JPanel();
        strokeColorPanel.setLayout(new BoxLayout(strokeColorPanel, BoxLayout.Y_AXIS));
        strokeColorPanel.setMaximumSize(new Dimension(120, 80));
        strokeColorPanel.setBackground(Color.BLACK);
        strokeColorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        strokeColorPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel strokeLabel = new JLabel("Stroke Color");
        strokeLabel.setForeground(Color.LIGHT_GRAY);
        strokeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        strokeColorPanel.add(strokeLabel);
        strokeColorPanel.add(Box.createVerticalStrut(5));

        strokeColorButton = new JButton();
        strokeColorButton.setBackground(document.getStrokeColor());
        strokeColorButton.setPreferredSize(new Dimension(120, 30));
        strokeColorButton.setMaximumSize(new Dimension(120, 30));
        strokeColorButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        strokeColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Stroke Color", 
                document.getStrokeColor());
            if (newColor != null) {
                document.setStrokeColor(newColor);
                strokeColorButton.setBackground(newColor);
            }
        });
        strokeColorPanel.add(strokeColorButton);
        add(strokeColorPanel);
        
        // 선 두께
        JPanel strokeWidthPanel = new JPanel();
        strokeWidthPanel.setLayout(new BoxLayout(strokeWidthPanel, BoxLayout.Y_AXIS));
        strokeWidthPanel.setMaximumSize(new Dimension(120, 80));
        strokeWidthPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        strokeWidthPanel.setBackground(Color.BLACK);
        strokeWidthPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel strokeWidthLabel = new JLabel("Stroke Width");
        strokeWidthLabel.setForeground(Color.LIGHT_GRAY);
        strokeWidthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        strokeWidthPanel.add(strokeWidthLabel);
        strokeWidthPanel.add(Box.createVerticalStrut(5));

        SpinnerNumberModel model = new SpinnerNumberModel(document.getStrokeWidth(), 1, 20, 1);
        strokeWidthSpinner = new JSpinner(model);
        strokeWidthSpinner.setPreferredSize(new Dimension(120, 25));
        strokeWidthSpinner.setMaximumSize(new Dimension(120, 25));
        strokeWidthSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        strokeWidthSpinner.addChangeListener(e -> {
            document.setStrokeWidth((Integer) strokeWidthSpinner.getValue());
        });
        strokeWidthPanel.add(strokeWidthSpinner);
        add(strokeWidthPanel);
    }
} 