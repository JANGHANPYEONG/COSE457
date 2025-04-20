package com.vector.editor.tools;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.Color;
import com.vector.editor.shapes.LineShape;
import com.vector.editor.CanvasPanel;

public class LineTool implements Tool {
    private boolean active = false;
    private Point startPoint;
    private Point currentPoint;
    private boolean isDragging = false;
    private LineShape currentShape;
    private CanvasPanel canvas;

    public LineTool(CanvasPanel canvas) {
        this.canvas = canvas;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!active) return;
        
        startPoint = e.getPoint();
        currentPoint = startPoint;
        isDragging = true;
        
        // 새로운 선 도형 생성
        currentShape = new LineShape(startPoint.x, startPoint.y, currentPoint.x, currentPoint.y, 
            Color.BLACK, 1);
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
        
        currentPoint = e.getPoint();
        
        if (currentShape != null) {
            currentShape.setEndPoint(currentPoint.x, currentPoint.y);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // 선은 드래그로만 그릴 수 있음
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