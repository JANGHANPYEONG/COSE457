package com.vector.editor.tools;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.Color;
import javax.swing.JOptionPane;
import com.vector.editor.shapes.TextShape;
import com.vector.editor.CanvasPanel;
import com.vector.editor.core.ColorManager;
import com.vector.editor.core.Shape;
import java.awt.event.KeyEvent;

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
        
        // 이미 선택된 텍스트가 있는지 확인
        for (Shape shape : canvas.getShapes()) {
            if (shape instanceof TextShape && shape.contains(clickPoint.x, clickPoint.y)) {
                // 이미 있는 텍스트를 선택하고 편집
                TextShape textShape = (TextShape) shape;
                String newText = JOptionPane.showInputDialog("Edit text:", textShape.getText());
                if (newText != null && !newText.isEmpty()) {
                    textShape.setText(newText);
                }
                return;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!active) return;
        
        // 새로운 텍스트 입력 다이얼로그 표시
        String text = JOptionPane.showInputDialog("Enter text:");
        if (text != null && !text.isEmpty()) {
            Color color = ColorManager.getInstance().getCurrentColor();
            currentShape = new TextShape(clickPoint.x, clickPoint.y, 0, 0,
                color, color, 1,
                text, "Arial", 12);
            canvas.addShape(currentShape);
            currentShape = null;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // 텍스트 도구는 드래그를 사용하지 않음
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // mousePressed와 mouseReleased에서 처리
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

    @Override
    public void keyPressed(KeyEvent e) {

    }
} 