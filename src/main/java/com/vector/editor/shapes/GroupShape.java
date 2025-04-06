package com.vector.editor.shapes;

import com.vector.editor.core.Shape;
import java.awt.Color;
import java.awt.Graphics;
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
        // TODO: Implement group shape drawing
    }

    @Override
    public boolean contains(int px, int py) {
        // TODO: Implement point containment check for group
        return false;
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

    @Override
    public void move(int dx, int dy) {
        super.move(dx, dy);
        for (Shape shape : shapes) {
            shape.move(dx, dy);
        }
    }

    @Override
    public void resize(int dw, int dh) {
        super.resize(dw, dh);
        // TODO: Implement proportional resizing of child shapes
    }
} 