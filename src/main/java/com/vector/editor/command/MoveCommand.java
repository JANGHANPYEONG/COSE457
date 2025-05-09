package com.vector.editor.command;

import com.vector.editor.core.Shape;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveCommand implements Command {
    private final List<Shape> shapes;
    private final Map<Shape, Point> beforeStates;
    private final Map<Shape, Point> afterStates;

    public MoveCommand(Shape shape,
        Map<Shape, Point> beforeStates,
        Map<Shape, Point> afterStates) {
        this.shapes = List.of(shape);
        this.beforeStates = beforeStates;
        this.afterStates = afterStates;
    }

    public MoveCommand(List<Shape> shapes,
        Map<Shape, Point> beforeStates,
        Map<Shape, Point> afterStates) {
        this.shapes = new ArrayList<>(shapes);
        this.beforeStates = beforeStates;
        this.afterStates = afterStates;
    }

    @Override
    public void execute() {
        for (Shape shape : shapes) {
            Point after = afterStates.get(shape);
            shape.setPosition(after.x, after.y);
        }
    }

    @Override
    public void undo() {
        for (Shape shape : shapes) {
            Point before = beforeStates.get(shape);
            shape.setPosition(before.x, before.y);
        }
    }
}
