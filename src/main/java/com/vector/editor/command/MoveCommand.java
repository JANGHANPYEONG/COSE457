package com.vector.editor.command;

import com.vector.editor.core.Shape;
import java.util.ArrayList;
import java.util.List;

public class MoveCommand implements Command {
    private final List<Shape> shapes;
    private final int dx, dy;

    public MoveCommand(Shape shape, int dx, int dy) {
        this.shapes = List.of(shape);
        this.dx = dx;
        this.dy = dy;
    }

    public MoveCommand(List<Shape> shapes, int dx, int dy) {
        this.shapes = new ArrayList<>(shapes);
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void execute() {

    }

    @Override
    public void undo() {
        for (Shape shape: shapes) {
            shape.move(-dx, -dy);
        }
    }
}
