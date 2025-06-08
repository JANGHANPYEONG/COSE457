package com.vector.editor.model;

import com.vector.editor.model.shape.Shape;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ShapeCollection implements Serializable {
    private List<Shape> shapes;
    private PropertyChangeSupport support;

    public static final String PROPERTY_SHAPES = "shapes";

    public ShapeCollection() {
        this.shapes = new ArrayList<>();
        this.support = new PropertyChangeSupport(this);
    }

    public void addShape(Shape shape) {
        List<Shape> oldShapes = new ArrayList<>(shapes);
        shapes.add(shape);
        support.firePropertyChange(PROPERTY_SHAPES, oldShapes, shapes);
    }

    public void removeShape(Shape shape) {
        List<Shape> oldShapes = new ArrayList<>(shapes);
        shapes.remove(shape);
        support.firePropertyChange(PROPERTY_SHAPES, oldShapes, shapes);
    }

    public List<Shape> getShapes() {
        List<Shape> sortedShapes = new ArrayList<>(shapes);
        sortedShapes.sort(Comparator.comparingInt(Shape::getZOrder));
        return sortedShapes;
    }

    public Shape findShapeAt(int x, int y) {
        List<Shape> sortedShapes = getShapes();
        for (int i = sortedShapes.size() - 1; i >= 0; i--) {
            Shape shape = sortedShapes.get(i);
            if (shape.contains(x, y)) {
                return shape;
            }
        }
        return null;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
} 