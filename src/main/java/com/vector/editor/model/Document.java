package com.vector.editor.model;

import com.vector.editor.model.shape.Shape;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class Document implements Serializable {
    private static final long serialVersionUID = 1L;

    private final ShapeCollection shapes;
    private final SelectionManager selection;
    private final StyleManager style;
    private boolean modified;
    private PropertyChangeSupport support;

    public static final String PROPERTY_MODIFIED = "modified";

    public Document() {
        this.shapes = new ShapeCollection();
        this.selection = new SelectionManager();
        this.style = new StyleManager();
        this.support = new PropertyChangeSupport(this);
    }

    // ShapeCollection 위임 메서드
    public void addShape(Shape shape) {
        shapes.addShape(shape);
        setModified(true);
    }

    public void removeShape(Shape shape) {
        shapes.removeShape(shape);
        selection.deselect(shape);
        setModified(true);
    }

    public Shape findShapeAt(int x, int y) {
        return shapes.findShapeAt(x, y);
    }

    // SelectionManager 위임 메서드
    public void selectShape(Shape shape) {
        selection.select(shape);
    }

    public void deselectShape(Shape shape) {
        selection.deselect(shape);
    }

    public void clearSelection() {
        selection.clearSelection();
    }

    // StyleManager 위임 메서드
    public void setFillColor(java.awt.Color color) {
        style.setFillColor(color);
        setModified(true);
    }

    public void setStrokeColor(java.awt.Color color) {
        style.setStrokeColor(color);
        setModified(true);
    }

    public void setStrokeWidth(int width) {
        style.setStrokeWidth(width);
        setModified(true);
    }

    // Getter 메서드
    public java.util.List<Shape> getShapes() {
        return shapes.getShapes();
    }

    public java.util.List<Shape> getSelectedShapes() {
        return selection.getSelectedShapes();
    }

    public boolean isSelected(Shape shape) {
        return selection.isSelected(shape);
    }

    public java.awt.Color getFillColor() {
        return style.getFillColor();
    }

    public java.awt.Color getStrokeColor() {
        return style.getStrokeColor();
    }

    public int getStrokeWidth() {
        return style.getStrokeWidth();
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        boolean oldModified = this.modified;
        this.modified = modified;
        support.firePropertyChange(PROPERTY_MODIFIED, oldModified, modified);
    }

    // PropertyChangeListener 관리
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
        shapes.addPropertyChangeListener(listener);
        selection.addPropertyChangeListener(listener);
        style.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
        shapes.removePropertyChangeListener(listener);
        selection.removePropertyChangeListener(listener);
        style.removePropertyChangeListener(listener);
    }
} 