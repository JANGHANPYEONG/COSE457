package com.vector.editor.command;

import com.vector.editor.core.Shape;
import java.util.List;

public class RemoveShapeCommand implements Command {
    private final List<Shape> shapeList;
    private final Shape shape;

    public RemoveShapeCommand(List<Shape> shapeList, Shape shape) {
        this.shapeList = shapeList;
        this.shape = shape;
    }

    @Override
    public void execute() {
        shapeList.remove(shape);
    }

    @Override
    public void undo() {
        shapeList.add(shape);
    }

}
