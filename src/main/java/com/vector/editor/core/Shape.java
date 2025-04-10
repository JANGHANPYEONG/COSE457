package com.vector.editor.core;

import java.awt.Color;
import java.awt.Graphics;
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
        this.selected = true;
        notifyObservers();
    }

    public void deselect() {
        this.selected = false;
        notifyObservers();
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
} 