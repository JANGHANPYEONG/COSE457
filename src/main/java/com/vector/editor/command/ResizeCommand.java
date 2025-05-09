package com.vector.editor.command;

import com.vector.editor.core.Shape;
import com.vector.editor.core.Shape.HandlePosition;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResizeCommand implements Command {
    private final List<Shape> shapes;
    private final HandlePosition handlePosition;
    private final int dx, dy;

    // 각 도형의 이전 상태를 저장하는 맵
    private final Map<Shape, Rectangle> originalStates = new HashMap<>();

    public ResizeCommand(Shape shape, HandlePosition handlePosition, int dx, int dy) {
        this.shapes = List.of(shape);
        this.handlePosition = handlePosition;
        this.dx = dx;
        this.dy = dy;

        originalStates.put(shape, new Rectangle(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight()));
    }

    public ResizeCommand(List<Shape> shapes, HandlePosition handlePosition, int dx, int dy) {
        this.shapes = new ArrayList<>(shapes);
        this.handlePosition = handlePosition;
        this.dx = dx;
        this.dy = dy;

        for (Shape shape : shapes) {
            originalStates.put(shape, new Rectangle(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight()));
        }
    }

    @Override
    public void execute() {
        for (Shape shape : shapes) {
            Rectangle originalState = originalStates.get(shape);

            switch (handlePosition) {
                case TOP_LEFT -> {
                    shape.setPosition(originalState.x + dx, originalState.y + dy);
                    shape.setSize(originalState.width - dx, originalState.height - dy);
                }
                case TOP_RIGHT -> {
                    shape.setPosition(originalState.x, originalState.y + dy);
                    shape.setSize(originalState.width + dx, originalState.height - dy);
                }
                case BOTTOM_LEFT -> {
                    shape.setPosition(originalState.x + dx, originalState.y);
                    shape.setSize(originalState.width - dx, originalState.height + dy);
                }
                case BOTTOM_RIGHT -> {
                    shape.setSize(originalState.width + dx, originalState.height + dy);
                }
            }
        }
    }

    @Override
    public void undo() {
        for (Shape shape : shapes) {
            Rectangle originalState = originalStates.get(shape);
            shape.setPosition(originalState.x, originalState.y);
            shape.setSize(originalState.width, originalState.height);
        }
    }
}
