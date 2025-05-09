package com.vector.editor.command;

import com.vector.editor.core.Shape;
import java.util.List;

public class ResizeCommand implements Command {
    private final Shape shape;
    private final int dw, dh;

    public ResizeCommand(Shape shape, int dw, int dh) {
        this.shape = shape;
        this.dw = dw;
        this.dh = dh;
    }

    @Override
    public void execute() {
        shape.resize(dw, dh)
    }

    @Override
    public void undo() {
        shape.resize(-dw, -dh);
    }
}
