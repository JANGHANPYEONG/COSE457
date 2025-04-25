package com.vector.editor.core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public abstract class Shape {
    protected int x, y;
    protected int width, height;
    protected Color fillColor;
    protected Color strokeColor;
    protected int strokeWidth;
    protected boolean selected;
    protected int zOrder;
    protected List<ShapeObserver> observers;
    protected int originalZOrder;

    protected static int maxZOrder = 0;

    public Shape(int x, int y, int width, int height,
                 Color fillColor, Color strokeColor, int strokeWidth) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.selected = false;
        this.zOrder = 0;
        this.observers = new ArrayList<>();
    }

    public abstract void draw(Graphics g);
    public abstract boolean contains(int px, int py);

    public boolean intersects(Rectangle rect) {
        return rect.intersects(x, y, width, height);
    }

    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
        notifyObservers();
    }

    public void resize(int dw, int dh) {
        this.width += dw;
        this.height += dh;
        notifyObservers();
    }

    public void select() {
        if (!selected) { // 중복 호출 방지
            this.selected = true;
            originalZOrder = zOrder; // 기존 위치 저장
            setZOrder(9999); // 선택된 도형을 가장 앞으로
            notifyObservers();
        }
    }

    public void deselect() {
        if (selected) {
            this.selected = false;
            setZOrder(originalZOrder); // 기존 위치로
            notifyObservers();
        }
    }

    public void setZOrder(int zOrder) {
        this.zOrder = zOrder;
        notifyObservers();
    }

    public int getZOrder() {
        return zOrder;
    }

    public void addObserver(ShapeObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ShapeObserver observer) {
        observers.remove(observer);
    }

    protected void notifyObservers() {
        for (ShapeObserver observer : observers) {
            observer.onShapeChanged(this);
        }
    }

    // Getters and Setters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Color getFillColor() { return fillColor; }
    public Color getStrokeColor() { return strokeColor; }
    public int getStrokeWidth() { return strokeWidth; }
    public boolean isSelected() { return selected; }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
        notifyObservers();
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
        notifyObservers();
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        notifyObservers();
    }

    public Rectangle getBoundsWithStroke() {
        int inset = strokeWidth / 2;
        return new Rectangle(
            x - inset,
            y - inset,
            width + strokeWidth,
            height + strokeWidth
        );
    }

    protected void drawSelectionUI(Graphics2D g2) {
        Rectangle bounds = getBoundsWithStroke();

        // 선택 테두리
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(1));
        g2.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

        // 리사이징 핸들
        int handleSize = 8;
        int[][] handles = {
            {bounds.x - handleSize / 2, bounds.y - handleSize / 2}, // top-left
            {bounds.x + bounds.width - handleSize / 2, bounds.y - handleSize / 2}, // top-right
            {bounds.x - handleSize / 2, bounds.y + bounds.height - handleSize / 2}, // bottom-left
            {bounds.x + bounds.width - handleSize / 2, bounds.y + bounds.height - handleSize / 2} // bottom-right
        };

        for (int[] pos : handles) {
            g2.fillRect(pos[0], pos[1], handleSize, handleSize);
        }
    }
}