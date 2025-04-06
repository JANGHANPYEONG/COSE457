package com.vector.editor.shapes;

import com.vector.editor.core.Shape;
import java.awt.Color;
import java.awt.Graphics;

public class RectangleShape extends Shape {
    public RectangleShape(int x, int y, int width, int height,
                         Color fillColor, Color strokeColor, int strokeWidth) {
        super(x, y, width, height, fillColor, strokeColor, strokeWidth);
    }

    @Override
    public void draw(Graphics g) {
        // TODO: Implement rectangle drawing
    }

    @Override
    public boolean contains(int px, int py) {
        // TODO: Implement point containment check for rectangle
        return false;
    }
}
