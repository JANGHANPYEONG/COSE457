package com.vector.editor.shapes;

import com.vector.editor.core.Shape;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class RectangleShape extends Shape {
    public RectangleShape(int x, int y, int width, int height,
                         Color fillColor, Color strokeColor, int strokeWidth) {
        super(x, y, width, height, fillColor, strokeColor, strokeWidth);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(fillColor);
        g2.fillRect(x, y, width, height);

        g2.setColor(strokeColor);
        g2.setStroke(new BasicStroke(strokeWidth));
        g2.drawRect(x, y, width, height);

        if (selected) {
            g2.setColor(Color.BLUE);
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(x - 2, y - 2, width + 4, height + 4);
        }
    }

    @Override
    public boolean contains(int px, int py) {
        if ((px >= x && px <= x + width) && (py >= y && py <= y +height)) {
            return true;
        }

        return false;
    }
}
