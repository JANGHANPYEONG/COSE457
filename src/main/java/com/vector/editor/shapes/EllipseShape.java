package com.vector.editor.shapes;

import com.vector.editor.core.Shape;
import java.awt.Color;
import java.awt.Graphics;

public class EllipseShape extends Shape {
    public EllipseShape(int x, int y, int width, int height,
                       Color fillColor, Color strokeColor, int strokeWidth) {
        super(x, y, width, height, fillColor, strokeColor, strokeWidth);
    }

    @Override
    public void draw(Graphics g) {
        // TODO: Implement ellipse drawing
    }

    @Override
    public boolean contains(int px, int py) {
        // TODO: Implement point containment check for ellipse
        return false;
    }
} 