package com.vector.editor.model;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class StyleManager {
    private Color fillColor;
    private Color strokeColor;
    private int strokeWidth;
    private PropertyChangeSupport support;

    public static final String PROPERTY_FILL_COLOR = "fillColor";
    public static final String PROPERTY_STROKE_COLOR = "strokeColor";
    public static final String PROPERTY_STROKE_WIDTH = "strokeWidth";

    public StyleManager() {
        this.fillColor = Color.WHITE;
        this.strokeColor = Color.BLACK;
        this.strokeWidth = 1;
        this.support = new PropertyChangeSupport(this);
    }

    public void setFillColor(Color color) {
        Color oldColor = this.fillColor;
        this.fillColor = color;
        support.firePropertyChange(PROPERTY_FILL_COLOR, oldColor, color);
    }

    public void setStrokeColor(Color color) {
        Color oldColor = this.strokeColor;
        this.strokeColor = color;
        support.firePropertyChange(PROPERTY_STROKE_COLOR, oldColor, color);
    }

    public void setStrokeWidth(int width) {
        int oldWidth = this.strokeWidth;
        this.strokeWidth = width;
        support.firePropertyChange(PROPERTY_STROKE_WIDTH, oldWidth, width);
    }

    public Color getFillColor() {
        return fillColor;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
} 