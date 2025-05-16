package com.vector.editor.shapes;

import com.vector.editor.core.Shape;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class GroupShape extends Shape {
    private List<Shape> shapes;

    public GroupShape(int x, int y, int width, int height,
                     Color fillColor, Color strokeColor, int strokeWidth) {
        super(x, y, width, height, fillColor, strokeColor, strokeWidth);
        this.shapes = new ArrayList<>();
    }

    @Override
    public void draw(Graphics g) {
        for (Shape shape: shapes) {
            shape.draw(g);
        }

        if (isSelected()) {
            Graphics2D g2 = (Graphics2D) g;
            drawSelectionUI(g2);
        }
    }

    @Override
    public boolean contains(int px, int py) {
        Rectangle bounds = getGroupBounds();

        if ((px >= bounds.x && px <= bounds.x + bounds.width) && (py >= bounds.y && py <= bounds.y + bounds.height)) {
            return true;
        }

        return false;
    }

    @Override
    public void move(int dx, int dy) {
        super.move(dx, dy);
        for (Shape shape : shapes) {
            shape.move(dx, dy);
        }
    }

    @Override
    public void resize(int dw, int dh) {
        int oldX = x;
        int oldY = y;
        int oldWidth = width;
        int oldHeight = height;

        super.resize(dw, dh);

        int newWidth = width;
        int newHeight = height;

        double scaleX = oldWidth != 0 ? (double) newWidth / oldWidth : 1.0;
        double scaleY = oldHeight != 0 ? (double) newHeight / oldHeight : 1.0;

        for (Shape shape: shapes) {
            int relX = shape.getX() - oldX;
            int relY = shape.getY() - oldY;

            int newX = x + (int)(relX * scaleX);
            int newY = y + (int)(relY * scaleY);

            int newW = (int)(shape.getWidth() * scaleX);
            int newH = (int)(shape.getHeight() * scaleY);

            shape.move(newX - shape.getX(), newY - shape.getY());
            shape.resize(newW - shape.getWidth(), newH - shape.getHeight());
        }

        notifyObservers();
    }

    @Override
    public Rectangle getBoundsWithStroke() {
        return getGroupBounds();
    }

    private Rectangle getGroupBounds() {
        if (shapes.isEmpty()) return new Rectangle(x, y, width, height);

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Shape shape : shapes) {
            minX = Math.min(minX, shape.getX());
            minY = Math.min(minY, shape.getY());
            maxX = Math.max(maxX, shape.getX() + shape.getWidth());
            maxY = Math.max(maxY, shape.getY() + shape.getHeight());
        }

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
        notifyObservers();
    }

    public void removeShape(Shape shape) {
        shapes.remove(shape);
        notifyObservers();
    }

    public List<Shape> getShapes() {
        return new ArrayList<>(shapes);
    }

    public List<Shape> ungroup() {
        return new ArrayList<>(shapes);
    }

    @Override
    public void setPosition(int x, int y) {
        int dx = x - this.x;
        int dy = y - this.y;
        super.setPosition(x, y);
        for (Shape shape : shapes) {
            shape.move(dx, dy);
        }
    }

    @Override
    public void setSize(int width, int height) {
        int oldX = this.x;
        int oldY = this.y;
        int oldWidth = this.width;
        int oldHeight = this.height;

        super.setSize(width, height);

        double scaleX = oldWidth != 0 ? (double) width / oldWidth : 1.0;
        double scaleY = oldHeight != 0 ? (double) height / oldHeight : 1.0;

        for (Shape shape : shapes) {
            int relX = shape.getX() - oldX;
            int relY = shape.getY() - oldY;

            int newX = x + (int)(relX * scaleX);
            int newY = y + (int)(relY * scaleY);

            int newW = (int)(shape.getWidth() * scaleX);
            int newH = (int)(shape.getHeight() * scaleY);

            shape.setPosition(newX, newY);
            shape.setSize(newW, newH);
        }
    }
} 