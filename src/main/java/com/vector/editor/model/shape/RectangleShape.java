package com.vector.editor.model.shape;

import java.awt.*;

public class RectangleShape extends Shape {
    private static final long serialVersionUID = 1L;

    public RectangleShape(int x, int y, int width, int height, Color fillColor, Color strokeColor, int strokeWidth) {
        super(x, y, width, height, fillColor, strokeColor, strokeWidth);
    }

    @Override
    public void draw(Graphics2D g) {
        // 채우기 색상이 있는 경우 채우기
        if (fillColor != null) {
            g.setColor(fillColor);
            g.fillRect(x, y, width, height);
        }

        // 테두리 그리기
        g.setColor(strokeColor);
        g.setStroke(new BasicStroke(strokeWidth));
        g.drawRect(x, y, width, height);

        // 선택된 경우 선택 표시
        if (selected) {
            drawSelectionUI(g);
        }
    }

    @Override
    public boolean contains(int px, int py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }
} 