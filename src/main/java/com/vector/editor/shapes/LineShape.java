package com.vector.editor.shapes;

import com.vector.editor.core.Shape;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class LineShape extends Shape {

    public LineShape(int x, int y, int width, int height,
        Color fillColor, Color strokeColor, int strokeWidth) {
        super(x, y, width, height, fillColor, strokeColor, strokeWidth);
    }

    public LineShape(int x1, int y1, int x2, int y2, Color strokeColor, int strokeWidth) {
        super(x1, y1, x2 - x1, y2 - y1, strokeColor, strokeColor, strokeWidth);
    }

    public void setEndPoint(int x2, int y2) {
        this.width = x2 - this.x;
        this.height = y2 - this.y;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(strokeColor);
        g2.setStroke(new BasicStroke(strokeWidth));
        g2.drawLine(x, y, x + width, y + height);

        if (isSelected()) {
            g2.setColor(Color.BLUE);
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(x - 2, y - 2, width + 4, height + 4);
        }
    }

    @Override
    public boolean contains(int px, int py) {
        final int tolerance = 3; // 선 근처를 클릭해도 선택되게

        int x1 = x;
        int y1 = y;
        int x2 = x + width;
        int y2 = y + height;

        double distance = pointToLineDistance(px, py, x1, y1, x2, y2);
        if (distance <= tolerance) {
            return true;
        }

        return false;
    }

    private double pointToLineDistance(int px, int py, int x1, int y1, int x2, int y2) {
        double A = px - x1;
        double B = py - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = len_sq != 0 ? dot / len_sq : -1;

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        double dx = px - xx;
        double dy = py - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }
}