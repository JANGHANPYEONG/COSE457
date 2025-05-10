package com.vector.editor;

import com.vector.editor.core.ColorManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ColorPanel extends JPanel implements ColorManager.ColorChangeListener {
    private static final int COLOR_BUTTON_SIZE = 30;
    private static final Color[] DEFAULT_COLORS = {
        Color.BLACK, Color.WHITE, Color.RED, Color.GREEN, Color.BLUE,
        Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK
    };

    private ColorManager colorManager;
    private JPanel colorPreview;

    public ColorPanel() {
        this.colorManager = ColorManager.getInstance();
        this.colorManager.addColorChangeListener(this);
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 100));
        
        // Color preview panel
        colorPreview = new JPanel();
        colorPreview.setPreferredSize(new Dimension(200, 30));
        colorPreview.setBackground(colorManager.getCurrentColor());
        add(colorPreview, BorderLayout.NORTH);
        
        // Color palette panel
        JPanel palettePanel = new JPanel(new GridLayout(2, 5, 5, 5));
        palettePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        for (Color color : DEFAULT_COLORS) {
            JButton colorButton = createColorButton(color);
            palettePanel.add(colorButton);
        }
        
        add(palettePanel, BorderLayout.CENTER);
    }

    private JButton createColorButton(Color color) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(COLOR_BUTTON_SIZE, COLOR_BUTTON_SIZE));
        button.setBackground(color);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                colorManager.setCurrentColor(color);
            }
        });
        
        return button;
    }

    @Override
    public void onColorChanged(Color newColor) {
        colorPreview.setBackground(newColor);
        repaint();
    }
} 