package com.vector.editor.shapes;

import com.vector.editor.core.Shape;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
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
        // TODO: Implement free draw path drawing
    }

    @Override
    public boolean contains(int px, int py) {
        // TODO: Implement point containment check for free draw path
        return false;
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
} 