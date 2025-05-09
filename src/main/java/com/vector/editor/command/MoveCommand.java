package com.vector.editor.command;

import com.vector.editor.core.Shape;
import java.util.List;

public class MoveCommand implements Command {
    private final Shape shape;
    private final int dx, dy;

    public MoveCommand(Shape shape, int dx, int dy) {
        this.shape = shape;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void execute() {
        shape.move(dx, dy);
    }

    @Override
    public void undo() {
        shape.move(-dx, -dy);
    }
}
