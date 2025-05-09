package com.vector.editor.command;

import com.vector.editor.core.Shape;
import com.vector.editor.core.Shape.HandlePosition;

public class ResizeCommand implements Command {
    private final Shape shape;
    private final HandlePosition handlePosition;
    private final int dx, dy;

    // 이전 상태 저장
    private final int originalX, originalY;
    private final int originalWidth, originalHeight;

    public ResizeCommand(Shape shape, HandlePosition handlePosition, int dx, int dy) {
        this.shape = shape;
        this.handlePosition = handlePosition;
        this.dx = dx;
        this.dy = dy;

        this.originalX = shape.getX();
        this.originalY = shape.getY();
        this.originalWidth = shape.getWidth();
        this.originalHeight = shape.getHeight();
    }

    @Override
    public void execute() {
        switch (handlePosition) {
            case TOP_LEFT -> {
                shape.setPosition(originalX + dx, originalY + dy);
                shape.setSize(originalWidth - dx, originalHeight - dy);
            }
            case TOP_RIGHT -> {
                shape.setPosition(originalX, originalY + dy);
                shape.setSize(originalWidth + dx, originalHeight - dy);
            }
            case BOTTOM_LEFT -> {
                shape.setPosition(originalX + dx, originalY);
                shape.setSize(originalWidth - dx, originalHeight + dy);
            }
            case BOTTOM_RIGHT -> {
                shape.setSize(originalWidth + dx, originalHeight + dy);
            }
        }
    }

    @Override
    public void undo() {
        shape.setPosition(originalX, originalY);
        shape.setSize(originalWidth, originalHeight);
    }
}
