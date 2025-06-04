package com.vector.editor.command;

import com.vector.editor.model.Document;
import com.vector.editor.model.shape.Shape;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveCommand implements Command {
    private Document document;
    private final List<Shape> shapes;
    private final Map<Shape, Point> beforeStates;
    private final Map<Shape, Point> afterStates;

    public MoveCommand(Document document, Shape shape,
        Map<Shape, Point> beforeStates,
        Map<Shape, Point> afterStates) {
        this.document = document;
        this.shapes = List.of(shape);
        this.beforeStates = new HashMap<>(beforeStates);
        this.afterStates = new HashMap<>(afterStates);
    }

    public MoveCommand(Document document, List<Shape> shapes,
        Map<Shape, Point> beforeStates,
        Map<Shape, Point> afterStates) {
        this.document = document;
        this.shapes = new ArrayList<>(shapes);
        this.beforeStates = new HashMap<>(beforeStates);
        this.afterStates = new HashMap<>(afterStates);
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
