package com.vector.editor.tools;

import com.vector.editor.core.Shape;
import com.vector.editor.shapes.EllipseShape;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.Color;
import com.vector.editor.shapes.RectangleShape;
import com.vector.editor.CanvasPanel;
import com.vector.editor.core.ColorManager;

public class RectangleTool extends AbstractShapeTool {
    public RectangleTool(CanvasPanel canvas) {
        super(canvas);
    }

    @Override
    protected Shape createShape(int x, int y, int width, int height) {
        Color color = ColorManager.getInstance().getCurrentColor();
        return new RectangleShape(x, y, width, height, null, color, 1);
    }
}