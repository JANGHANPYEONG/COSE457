package com.vector.editor.shapes;

import com.vector.editor.core.Shape;
import java.awt.Color;
import java.awt.Graphics;

public class LineShape extends Shape {
    public LineShape(int x, int y, int width, int height,
                    Color fillColor, Color strokeColor, int strokeWidth) {
        super(x, y, width, height, fillColor, strokeColor, strokeWidth);
    }

    @Override
    public void draw(Graphics g) {
        // TODO: Implement line drawing
    }

    @Override
    public boolean contains(int px, int py) {
        // TODO: Implement point containment check for line
        return false;
    }
} 