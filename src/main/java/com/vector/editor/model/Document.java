package com.vector.editor.model;

import com.vector.editor.model.shape.Shape;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class Document implements Serializable {
    private static final long serialVersionUID = 1L;

    private final ShapeCollection shapes;
    private final SelectionManager selection;
    private final StyleManager style;
    private boolean modified;
    private PropertyChangeSupport support;

    public static final String PROPERTY_MODIFIED = "modified";
    public static final String PROPERTY_ZORDER = "zOrder";

    public Document() {
        this.shapes = new ShapeCollection();
        this.selection = new SelectionManager();
        this.style = new StyleManager();
        this.support = new PropertyChangeSupport(this);

        this.selection.addPropertyChangeListener(evt -> {
            if (SelectionManager.PROPERTY_SELECTED_SHAPES.equals(evt.getPropertyName())) {
                support.firePropertyChange("selection", evt.getOldValue(), evt.getNewValue());
            }
        });
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

    // Z-Order 관리 메서드들
    public void bringToFront(List<Shape> shapes) {
        List<Shape> allShapes = new ArrayList<>(getShapes());
        allShapes.sort(Comparator.comparingInt(Shape::getZOrder));

        // 선택된 도형들을 제외한 나머지 도형들의 zOrder를 조정
        int baseZOrder = 0;
        for (Shape shape : allShapes) {
            if (!shapes.contains(shape)) {
                shape.setZOrder(baseZOrder++);
            }
        }
        // 선택된 도형들을 맨 앞으로
        for (Shape shape : shapes) {
            shape.setZOrder(baseZOrder++);
        }
        support.firePropertyChange(PROPERTY_ZORDER, null, shapes);
        setModified(true);
    }

    public void sendToBack(List<Shape> shapes) {
        List<Shape> allShapes = new ArrayList<>(getShapes());
        allShapes.sort(Comparator.comparingInt(Shape::getZOrder));

        // 선택된 도형들을 맨 뒤로
        int baseZOrder = 0;
        for (Shape shape : shapes) {
            shape.setZOrder(baseZOrder++);
        }
        // 나머지 도형들의 zOrder를 조정
        for (Shape shape : allShapes) {
            if (!shapes.contains(shape)) {
                shape.setZOrder(baseZOrder++);
            }
        }
        support.firePropertyChange(PROPERTY_ZORDER, null, shapes);
        setModified(true);
    }

    public void bringForward(List<Shape> shapes) {
        List<Shape> allShapes = new ArrayList<>(getShapes());
        allShapes.sort(Comparator.comparingInt(Shape::getZOrder));

        // 선택된 도형들의 현재 위치를 찾고, 한 칸씩 앞으로 이동
        for (Shape shape : shapes) {
            int currentIndex = allShapes.indexOf(shape);
            if (currentIndex < allShapes.size() - 1) {
                Shape nextShape = allShapes.get(currentIndex + 1);
                if (!shapes.contains(nextShape)) {
                    int tempZOrder = shape.getZOrder();
                    shape.setZOrder(nextShape.getZOrder());
                    nextShape.setZOrder(tempZOrder);
                }
            }
        }
        support.firePropertyChange(PROPERTY_ZORDER, null, shapes);
        setModified(true);
    }

    public void sendBackward(List<Shape> shapes) {
        List<Shape> allShapes = new ArrayList<>(getShapes());
        allShapes.sort(Comparator.comparingInt(Shape::getZOrder));

        // 선택된 도형들의 현재 위치를 찾고, 한 칸씩 뒤로 이동
        for (Shape shape : shapes) {
            int currentIndex = allShapes.indexOf(shape);
            if (currentIndex > 0) {
                Shape prevShape = allShapes.get(currentIndex - 1);
                if (!shapes.contains(prevShape)) {
                    int tempZOrder = shape.getZOrder();
                    shape.setZOrder(prevShape.getZOrder());
                    prevShape.setZOrder(tempZOrder);
                }
            }
        }
        support.firePropertyChange(PROPERTY_ZORDER, null, shapes);
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