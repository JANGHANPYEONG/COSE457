package com.vector.editor.shapes;

import com.vector.editor.core.Shape;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class FreeDrawPath extends Shape {
    private List<Point> points;
    private boolean isClosed;

    public FreeDrawPath(int x, int y, int width, int height,
                       Color fillColor, Color strokeColor, int strokeWidth) {
        super(x, y, width, height, fillColor, strokeColor, strokeWidth);
        this.points = new ArrayList<>();
        this.isClosed = false;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(strokeColor);
        g2.setStroke(new BasicStroke(strokeWidth));

        if (points.size() < 2) return;

        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        if (isSelected()) {
            drawSelectionUI(g2);
        }
    }

    @Override
    public boolean contains(int px, int py) {
        final int tolerance = 4;

        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            double dist = pointToLineDistance(px, py, p1.x, p1.y, p2.x, p2.y);
            if (dist <= tolerance) return true;
        }

        return false;
    }

    @Override
    public Rectangle getBoundsWithStroke() {
        Rectangle base = getBoundsFromPoints();
        int inset = strokeWidth / 2;
        return new Rectangle(
            base.x - inset,
            base.y - inset,
            base.width + strokeWidth,
            base.height + strokeWidth
        );
    }

    public void addPoint(Point point) {
        points.add(point);
        notifyObservers();
    }

    public List<Point> getPoints() {
        return new ArrayList<>(points);
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
        notifyObservers();
    }

    private Rectangle getBoundsFromPoints() {
        if (points.isEmpty()) return new Rectangle(x, y, width, height);

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Point p : points) {
            if (p.x < minX) minX = p.x;
            if (p.x > maxX) maxX = p.x;
            if (p.y < minY) minY = p.y;
            if (p.y > maxY) maxY = p.y;
        }

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
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