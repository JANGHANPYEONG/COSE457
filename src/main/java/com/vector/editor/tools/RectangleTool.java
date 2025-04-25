package com.vector.editor.tools;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.Color;
import com.vector.editor.shapes.RectangleShape;
import com.vector.editor.CanvasPanel;

public class RectangleTool implements Tool {
    private boolean active = false;
    private Point startPoint;
    private Point currentPoint;
    private boolean isDragging = false;
    private RectangleShape currentShape;
    private CanvasPanel canvas;

    public RectangleTool(CanvasPanel canvas) {
        this.canvas = canvas;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!active) return;
        
        startPoint = e.getPoint();
        currentPoint = startPoint;
        isDragging = true;
        
        // 새로운 사각형 도형 생성
        currentShape = new RectangleShape(startPoint.x, startPoint.y, 0, 0, 
            Color.WHITE, Color.BLACK, 1);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!active) return;
        
        currentPoint = e.getPoint();
        isDragging = false;
        
        if (currentShape != null) {
            canvas.addShape(currentShape);
            currentShape = null;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!active) return;
        
        Point newPoint = e.getPoint();
        int dw = newPoint.x - currentPoint.x;
        int dh = newPoint.y - currentPoint.y;
        currentPoint = newPoint;
        
        if (currentShape != null) {
            currentShape.resize(dw, dh);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // 사각형은 드래그로만 그릴 수 있음
    }

    @Override
    public void draw(Graphics g) {
        if (!active || !isDragging || currentShape == null) return;
        
        currentShape.draw(g);
    }

    @Override
    public void activate() {
        active = true;
    }

    @Override
    public void deactivate() {
        active = false;
        isDragging = false;
        currentShape = null;
    }

    @Override
    public boolean isActive() {
        return active;
    }
} 