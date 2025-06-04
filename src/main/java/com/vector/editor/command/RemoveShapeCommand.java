package com.vector.editor.command;

import com.vector.editor.model.Document;
import com.vector.editor.model.shape.Shape;

public class RemoveShapeCommand implements Command {
    private Document document;
    private Shape shape;

    public RemoveShapeCommand(Document document, Shape shape) {
        this.document = document;
        this.shape = shape;
    }

    @Override
    public void execute() {
        document.removeShape(shape);
    }

    @Override
    public void undo() {
        document.addShape(shape);
    }
}
