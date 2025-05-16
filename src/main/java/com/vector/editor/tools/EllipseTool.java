package com.vector.editor.tools;

import java.awt.Color;
import com.vector.editor.core.Shape;
import com.vector.editor.shapes.EllipseShape;
import com.vector.editor.CanvasPanel;
import com.vector.editor.core.ColorManager;

public class EllipseTool extends AbstractShapeTool {
    public EllipseTool(CanvasPanel canvas) {
        super(canvas);
    }

    @Override
    protected Shape createShape(int x, int y, int width, int height) {
        Color color = ColorManager.getInstance().getCurrentColor();
        return new EllipseShape(x, y, width, height, null, color, 1);
    }
}
