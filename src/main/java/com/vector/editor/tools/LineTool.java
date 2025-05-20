package com.vector.editor.tools;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.Color;
import com.vector.editor.shapes.LineShape;
import com.vector.editor.CanvasPanel;
import com.vector.editor.core.ColorManager;
import com.vector.editor.core.Shape;

public class LineTool extends AbstractShapeTool {
    public LineTool(CanvasPanel canvas) {
        super(canvas);
    }

    @Override
    protected Shape createShape(int x, int y, int width, int height) {
        Color color = ColorManager.getInstance().getCurrentColor();
        return new LineShape(x, y, x + width, y + height, color, 1);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!active || currentShape == null) return;

        currentPoint = e.getPoint();
        
        // 선의 경우 width와 height를 사용하지 않고 직접 끝점을 설정
        if (currentShape instanceof LineShape) {
            ((LineShape) currentShape).setEndPoint(currentPoint.x, currentPoint.y);
        }

        canvas.repaint();
    }
} 