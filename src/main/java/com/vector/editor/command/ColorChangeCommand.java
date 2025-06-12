package com.vector.editor.command;

import java.awt.Color;
import com.vector.editor.model.shape.Shape;

public class ColorChangeCommand implements Command {
    private final Shape shape;
    private final Color oldColor;
    private final Color newColor;
    private final boolean isFill;

    public ColorChangeCommand(Shape shape, Color oldColor, Color newColor, boolean isFill) {
        this.shape = shape;
        this.oldColor = oldColor;
        this.newColor = newColor;
        this.isFill = isFill;
    }

    @Override
    public void execute() {
        if (isFill) {
            shape.setFillColor(newColor);
        } else {
            shape.setStrokeColor(newColor);
        }
    }

    @Override
    public void undo() {
        if (isFill) {
            shape.setFillColor(oldColor);
        } else {
            shape.setStrokeColor(oldColor);
        }
    }
}

