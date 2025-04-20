package com.vector.editor.tools;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.ArrayList;
import com.vector.editor.core.Shape;
import com.vector.editor.CanvasPanel;

public class SelectionTool implements Tool {
    private boolean active = false;
    private Point startPoint;
    private Point currentPoint;
    private boolean isDragging = false;
    private List<Shape> selectedShapes;
    private CanvasPanel canvas;

    public SelectionTool(CanvasPanel canvas) {
        this.canvas = canvas;
        this.selectedShapes = new ArrayList<>();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!active) return;
        
        startPoint = e.getPoint();
        currentPoint = startPoint;
        isDragging = true;
        
        // 선택 영역 내의 도형들을 찾아서 selectedShapes에 추가
        Rectangle selectionRect = new Rectangle(
            Math.min(startPoint.x, currentPoint.x),
            Math.min(startPoint.y, currentPoint.y),
            Math.abs(currentPoint.x - startPoint.x),
            Math.abs(currentPoint.y - startPoint.y)
        );
        
        selectedShapes.clear();
        for (Shape shape : canvas.getShapes()) {
            if (shape.intersects(selectionRect)) {
                selectedShapes.add(shape);
                shape.select();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!active) return;
        
        currentPoint = e.getPoint();
        isDragging = false;
        
        // 선택 영역 내의 도형들을 최종적으로 선택
        Rectangle selectionRect = new Rectangle(
            Math.min(startPoint.x, currentPoint.x),
            Math.min(startPoint.y, currentPoint.y),
            Math.abs(currentPoint.x - startPoint.x),
            Math.abs(currentPoint.y - startPoint.y)
        );
        
        for (Shape shape : canvas.getShapes()) {
            if (shape.intersects(selectionRect)) {
                if (!selectedShapes.contains(shape)) {
                    selectedShapes.add(shape);
                    shape.select();
                }
            } else {
                if (selectedShapes.contains(shape)) {
                    selectedShapes.remove(shape);
                    shape.deselect();
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!active) return;
        
        currentPoint = e.getPoint();
        
        // 드래그 중 선택 영역 업데이트
        Rectangle selectionRect = new Rectangle(
            Math.min(startPoint.x, currentPoint.x),
            Math.min(startPoint.y, currentPoint.y),
            Math.abs(currentPoint.x - startPoint.x),
            Math.abs(currentPoint.y - startPoint.y)
        );
        
        for (Shape shape : canvas.getShapes()) {
            if (shape.intersects(selectionRect)) {
                if (!selectedShapes.contains(shape)) {
                    selectedShapes.add(shape);
                    shape.select();
                }
            } else {
                if (selectedShapes.contains(shape)) {
                    selectedShapes.remove(shape);
                    shape.deselect();
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!active) return;
        
        Point clickPoint = e.getPoint();
        
        // 클릭한 위치의 도형을 선택
        for (Shape shape : canvas.getShapes()) {
            if (shape.contains(clickPoint.x, clickPoint.y)) {
                if (!selectedShapes.contains(shape)) {
                    selectedShapes.add(shape);
                    shape.select();
                }
            } else {
                if (selectedShapes.contains(shape)) {
                    selectedShapes.remove(shape);
                    shape.deselect();
                }
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        if (!active || !isDragging) return;
        
        int x = Math.min(startPoint.x, currentPoint.x);
        int y = Math.min(startPoint.y, currentPoint.y);
        int width = Math.abs(currentPoint.x - startPoint.x);
        int height = Math.abs(currentPoint.y - startPoint.y);
        
        // 선택 영역을 점선으로 그리기
        g.setXORMode(java.awt.Color.WHITE);
        g.drawRect(x, y, width, height);
        g.setPaintMode();
    }

    @Override
    public void activate() {
        active = true;
    }

    @Override
    public void deactivate() {
        active = false;
        isDragging = false;
        // 선택 해제
        for (Shape shape : selectedShapes) {
            shape.deselect();
        }
        selectedShapes.clear();
    }

    @Override
    public boolean isActive() {
        return active;
    }
} 