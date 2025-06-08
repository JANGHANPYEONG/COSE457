package com.vector.editor.model;

import com.vector.editor.model.shape.Shape;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectionManager implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Shape> selectedShapes;
    private PropertyChangeSupport support;

    public static final String PROPERTY_SELECTED_SHAPES = "selectedShapes";

    public SelectionManager() {
        this.selectedShapes = new ArrayList<>();
        this.support = new PropertyChangeSupport(this);
    }

    public void select(Shape shape) {
        if (!selectedShapes.contains(shape)) {
            List<Shape> oldSelected = new ArrayList<>(selectedShapes);
            selectedShapes.add(shape);
            shape.setSelected(true);
            support.firePropertyChange(PROPERTY_SELECTED_SHAPES, oldSelected, selectedShapes);
        }
    }

    public void deselect(Shape shape) {
        if (selectedShapes.contains(shape)) {
            List<Shape> oldSelected = new ArrayList<>(selectedShapes);
            selectedShapes.remove(shape);
            shape.setSelected(false);
            support.firePropertyChange(PROPERTY_SELECTED_SHAPES, oldSelected, selectedShapes);
        }
    }

    public void clearSelection() {
        List<Shape> oldSelected = new ArrayList<>(selectedShapes);
        for (Shape shape : selectedShapes) {
            shape.setSelected(false);
        }
        selectedShapes.clear();
        support.firePropertyChange(PROPERTY_SELECTED_SHAPES, oldSelected, selectedShapes);
    }

    public List<Shape> getSelectedShapes() {
        return new ArrayList<>(selectedShapes);
    }

    public boolean isSelected(Shape shape) {
        return selectedShapes.contains(shape);
    }

    public boolean hasSelection() {
        return !selectedShapes.isEmpty();
    }

    public int getSelectionCount() {
        return selectedShapes.size();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
} 