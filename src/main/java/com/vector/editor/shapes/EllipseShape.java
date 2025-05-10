package com.vector.editor.shapes;

import com.vector.editor.core.Shape;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class EllipseShape extends Shape {
    public EllipseShape(int x, int y, int width, int height,
                       Color fillColor, Color strokeColor, int strokeWidth) {
        super(x, y, width, height, fillColor, strokeColor, strokeWidth);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g; // Graphics2D로 변환

        // 도형 내부 채우기
        if (fillColor != null) {
            g2.setColor(fillColor);
            g2.fillOval(x, y, width, height);
        }

        // 도형 외곽선
        g2.setColor(strokeColor); // 색
        g2.setStroke(new BasicStroke(strokeWidth)); // 두께
        g2.drawOval(x, y, width, height);

        if (isSelected()) {
            drawSelectionUI(g2);
        }
    }

    @Override
    public boolean contains(int px, int py) {
        // 타원의 중심
        double cx = x + width / 2.0;
        double cy = y + height / 2.0;

        double dx = px - cx;
        double dy = py - cy;
        double rx = width / 2.0;
        double ry = height / 2.0;

        if ((dx * dx) / (rx * rx) + (dy * dy) / (ry * ry) <= 1.0) {
            return true;
        }

        return false;
    }
} 