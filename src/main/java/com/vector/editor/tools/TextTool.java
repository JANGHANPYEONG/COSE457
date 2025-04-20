package com.vector.editor.tools;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.Color;
import javax.swing.JOptionPane;
import com.vector.editor.shapes.TextShape;
import com.vector.editor.CanvasPanel;

public class TextTool implements Tool {
    private boolean active = false;
    private Point clickPoint;
    private TextShape currentShape;
    private CanvasPanel canvas;

    public TextTool(CanvasPanel canvas) {
        this.canvas = canvas;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!active) return;
        
        clickPoint = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // 텍스트 도구는 mousePressed에서 처리
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // 텍스트 도구는 드래그를 사용하지 않음
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!active) return;
        
        clickPoint = e.getPoint();
        
        // 텍스트 입력 다이얼로그 표시
        String text = JOptionPane.showInputDialog("Enter text:");
        if (text != null && !text.isEmpty()) {
            currentShape = new TextShape(clickPoint.x, clickPoint.y, 0, 0,
                Color.BLACK, Color.BLACK, 1,
                text, "Arial", 12);
            canvas.addShape(currentShape);
            currentShape = null;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (!active || currentShape == null) return;
        
        currentShape.draw(g);
    }

    @Override
    public void activate() {
        active = true;
    }

    @Override
    public void deactivate() {
        active = false;
        currentShape = null;
    }

    @Override
    public boolean isActive() {
        return active;
    }
} 