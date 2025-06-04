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
        
        setupUI();
    }

    private void setupUI() {
        // 채우기 색상
        JPanel fillColorPanel = new JPanel();
        fillColorPanel.add(new JLabel("Fill Color:"));
        fillColorButton = new JButton();
        fillColorButton.setBackground(document.getFillColor());
        fillColorButton.setPreferredSize(new Dimension(50, 25));
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
        strokeColorPanel.add(new JLabel("Stroke Color:"));
        strokeColorButton = new JButton();
        strokeColorButton.setBackground(document.getStrokeColor());
        strokeColorButton.setPreferredSize(new Dimension(50, 25));
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
        strokeWidthPanel.add(new JLabel("Stroke Width:"));
        SpinnerNumberModel model = new SpinnerNumberModel(document.getStrokeWidth(), 1, 20, 1);
        strokeWidthSpinner = new JSpinner(model);
        strokeWidthSpinner.addChangeListener(e -> {
            document.setStrokeWidth((Integer) strokeWidthSpinner.getValue());
        });
        strokeWidthPanel.add(strokeWidthSpinner);
        add(strokeWidthPanel);
    }
} 