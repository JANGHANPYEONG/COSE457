package com.vector.editor.command;

import com.vector.editor.CanvasPanel;
import com.vector.editor.core.Shape;
import com.vector.editor.core.Shape.HandlePosition;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResizeCommand implements Command {
    private final List<Shape> shapes;
    private final Map<Shape, Rectangle> before = new HashMap<>();
    private final Map<Shape, Rectangle> after = new HashMap<>();

    public ResizeCommand(List<Shape> shapes, Map<Shape, Rectangle> beforeStates, Map<Shape, Rectangle> afterStates) {
        this.shapes = new ArrayList<>(shapes);
        for (Shape shape : shapes) {
            before.put(shape, new Rectangle(beforeStates.get(shape)));
            after.put(shape, new Rectangle(afterStates.get(shape)));
        }
    }

    @Override
    public void execute() {
        for (Shape shape : shapes) {
            Rectangle r = after.get(shape);
            shape.setPosition(r.x, r.y);
            shape.setSize(r.width, r.height);
        }
    }

    @Override
    public void undo() {
        for (Shape shape : shapes) {
            Rectangle r = before.get(shape);
            shape.setPosition(r.x, r.y);
            shape.setSize(r.width, r.height);
        }
    }
}

