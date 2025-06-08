package com.vector.editor.command;

import com.vector.editor.model.Document;
import com.vector.editor.model.shape.Shape;

public class AddShapeCommand implements Command {
    private Document document;
    private Shape shape;

    public AddShapeCommand(Document document, Shape shape) {
        this.document = document;
        this.shape = shape;
    }

    @Override
    public void execute() {
        document.addShape(shape);
    }

    @Override
    public void undo() {
        document.removeShape(shape);
    }
}
