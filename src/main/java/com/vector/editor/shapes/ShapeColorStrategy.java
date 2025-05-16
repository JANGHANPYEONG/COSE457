package com.vector.editor.shapes;

import java.awt.Color;
import java.awt.Graphics2D;

public interface ShapeColorStrategy {
    void applyColor(Graphics2D g2d, Color color);
} 