package com.vector.editor.model.shape;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.beans.PropertyChangeSupport;

public class GroupShape extends Shape {
    private List<Shape> shapes;
    private PropertyChangeSupport support;

    public GroupShape() {
        super(0, 0, 0, 0, null, null, 0);
        this.shapes = new ArrayList<>();
        this.support = new PropertyChangeSupport(this);
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
        updateBounds();
        support.firePropertyChange("shapes", null, shapes);
    }

    public void removeShape(Shape shape) {
        shapes.remove(shape);
        updateBounds();
        support.firePropertyChange("shapes", null, shapes);
    }

    public List<Shape> getShapes() {
        return new ArrayList<>(shapes);
    }

    @Override
    public void draw(Graphics2D g) {
        // 그룹 내 모든 도형 그리기
        for (Shape shape : shapes) {
            shape.draw(g);
        }

        // 선택된 경우 선택 표시
        if (selected) {
            drawSelectionUI(g);
        }
    }

    @Override
    public boolean contains(int px, int py) {
        // 그룹 내 어떤 도형이라도 포함하면 true
        for (Shape shape : shapes) {
            if (shape.contains(px, py)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setPosition(int x, int y) {
        int dx = x - this.x;
        int dy = y - this.y;
        
        // 모든 도형의 위치 이동
        for (Shape shape : shapes) {
            shape.setPosition(shape.getX() + dx, shape.getY() + dy);
        }
        
        super.setPosition(x, y);
    }

    @Override
    public void setSize(int width, int height) {
        double scaleX = (double) width / this.width;
        double scaleY = (double) height / this.height;
        
        // 모든 도형의 크기 조정
        for (Shape shape : shapes) {
            int newWidth = (int) (shape.getWidth() * scaleX);
            int newHeight = (int) (shape.getHeight() * scaleY);
            shape.setSize(newWidth, newHeight);
        }
        
        super.setSize(width, height);
    }

    private void updateBounds() {
        if (shapes.isEmpty()) {
            x = y = width = height = 0;
            return;
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Shape shape : shapes) {
            Rectangle bounds = shape.getBounds();
            minX = Math.min(minX, bounds.x);
            minY = Math.min(minY, bounds.y);
            maxX = Math.max(maxX, bounds.x + bounds.width);
            maxY = Math.max(maxY, bounds.y + bounds.height);
        }

        x = minX;
        y = minY;
        width = maxX - minX;
        height = maxY - minY;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        // 그룹 내 모든 도형의 선택 상태도 변경
        for (Shape shape : shapes) {
            shape.setSelected(selected);
        }
    }
} 