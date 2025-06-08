package com.vector.editor.model.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;


public class EllipseShape extends Shape {
    private static final long serialVersionUID = 1L;

    public EllipseShape(int x, int y, int width, int height, Color fillColor, Color strokeColor, int strokeWidth) {
        super(x, y, width, height, fillColor, strokeColor, strokeWidth);
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(fillColor);
        g2d.fillOval(x, y, width, height);
        g2d.setColor(strokeColor);
        g2d.setStroke(new java.awt.BasicStroke(strokeWidth));
        g2d.drawOval(x, y, width, height);

        // 선택된 경우 선택 표시
        if (selected) {
            drawSelectionUI(g2d);
        }
    }

    @Override
    public boolean contains(int x, int y) {
        return new Ellipse2D.Float(this.x, this.y, width, height).contains(x, y);
    }
} 