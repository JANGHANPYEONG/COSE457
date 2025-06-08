package com.vector.editor.controller;

import com.vector.editor.command.MoveCommand;
import com.vector.editor.command.ResizeCommand;
import com.vector.editor.model.Document;
import com.vector.editor.model.shape.Shape;
import com.vector.editor.command.CommandManager;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateManager {
    private final Document document;
    private final CommandManager commandManager;
    private final PropertyChangeSupport support;

    // 현재 선택된 도형의 상태 추적
    private Shape currentShape;

    // 속성 이름 상수
    public static final String PROPERTY_CURRENT_SHAPE = "currentShape";
    public static final String PROPERTY_POSITION_CHANGED = "positionChanged";
    public static final String PROPERTY_SIZE_CHANGED = "sizeChanged";

    public StateManager(Document document, CommandManager commandManager) {
        this.document = document;
        this.commandManager = commandManager;
        this.support = new PropertyChangeSupport(this);

        // Document의 선택 변경 이벤트 리스닝
        document.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("selection".equals(evt.getPropertyName())) {
                    updateCurrentShape();
                }
            }
        });

        updateCurrentShape();
    }

    private void updateCurrentShape() {
        Shape oldShape = this.currentShape;

        // 기존 도형의 PropertyChangeListener 제거
        if (oldShape != null) {
            oldShape.removePropertyChangeListener(shapePropertyListener);
        }

        // 새 도형 설정
        List<Shape> selected = document.getSelectedShapes();
        this.currentShape = selected.isEmpty() ? null : selected.get(0);

        // 새 도형에 PropertyChangeListener 추가
        if (this.currentShape != null) {
            this.currentShape.addPropertyChangeListener(shapePropertyListener);
        }

        support.firePropertyChange(PROPERTY_CURRENT_SHAPE, oldShape, this.currentShape);
    }

    // 도형의 속성 변경을 감지하는 리스너
    private final PropertyChangeListener shapePropertyListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (Shape.PROPERTY_POSITION.equals(evt.getPropertyName())) {
                support.firePropertyChange(PROPERTY_POSITION_CHANGED, evt.getOldValue(), evt.getNewValue());
            } else if (Shape.PROPERTY_SIZE.equals(evt.getPropertyName())) {
                support.firePropertyChange(PROPERTY_SIZE_CHANGED, evt.getOldValue(), evt.getNewValue());
            }
        }
    };

    public void setPosition(int x, int y) {
        List<Shape> selected = document.getSelectedShapes();
        if (selected.isEmpty()) return;

        Map<Shape, Point> before = new HashMap<>();
        Map<Shape, Point> after = new HashMap<>();

        for (Shape s : selected) {
            before.put(s, new Point(s.getX(), s.getY()));
            after.put(s, new Point(x, y));
        }

        commandManager.executeCommand(new MoveCommand(document, selected, before, after));
    }

    public void setSize(int width, int height) {
        List<Shape> selected = document.getSelectedShapes();
        if (selected.isEmpty()) return;

        Map<Shape, Rectangle> before = new HashMap<>();
        Map<Shape, Rectangle> after = new HashMap<>();

        for (Shape s : selected) {
            before.put(s, new Rectangle(s.getX(), s.getY(), s.getWidth(), s.getHeight()));
            after.put(s, new Rectangle(s.getX(), s.getY(), width, height));
        }

        commandManager.executeCommand(new ResizeCommand(document, selected, before, after));
    }

    // 현재 선택된 도형 반환
    public Shape getCurrentShape() {
        return currentShape;
    }

    // PropertyChangeListener 관리
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        support.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        support.removePropertyChangeListener(propertyName, listener);
    }
}