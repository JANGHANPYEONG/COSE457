package com.vector.editor.tools;

import com.vector.editor.CanvasPanel;
import com.vector.editor.core.Shape;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract class AbstractShapeTool implements Tool {
    protected boolean active = false;
    protected Point startPoint;
    protected Point currentPoint;
    protected boolean isDragging = false;

    protected Shape currentShape;
    protected CanvasPanel canvas;

    public AbstractShapeTool(CanvasPanel canvas) {
        this.canvas = canvas;
    }

    protected abstract Shape createShape(int x, int y, int width, int height);

    @Override
    public void mousePressed(MouseEvent e) {
        if (!active) return;

        startPoint = e.getPoint();
        currentPoint = startPoint;
        isDragging = true;

        currentShape = createShape(startPoint.x, startPoint.y, 0, 0);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!active || currentShape == null) return;

        currentPoint = e.getPoint();

        int x = Math.min(startPoint.x, currentPoint.x);
        int y = Math.min(startPoint.y, currentPoint.y);
        int w = Math.abs(currentPoint.x - startPoint.x);
        int h = Math.abs(currentPoint.y - startPoint.y);

        currentShape.setPosition(x, y);
        currentShape.setSize(w, h);

        canvas.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!active || currentShape == null) return;

        isDragging = false;
        canvas.addShape(currentShape);
        currentShape = null;
        canvas.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Optional: 클릭으로 생성하는 도형에만 필요
    }

    @Override
    public void draw(Graphics g) {
        if (!active || !isDragging || currentShape == null) return;
        currentShape.draw((Graphics2D) g);
    }

    @Override
    public void activate() { active = true; }

    @Override
    public void deactivate() {
        active = false;
        isDragging = false;
        currentShape = null;
    }

    @Override
    public boolean isActive() { return active; }

    @Override
    public void keyPressed(KeyEvent e) {
        // 기본 구현은 아무것도 하지 않음
    }
}

