package com.vector.editor.tools;

import com.vector.editor.CanvasPanel;
import com.vector.editor.shapes.FreeDrawPath;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import com.vector.editor.core.ColorManager;

public class FreeDrawTool implements Tool {
    private Color strokeColor;
    private int strokeWidth;
    private CanvasPanel canvas;
    private FreeDrawPath currentPath;
    private boolean active = false;

    public FreeDrawTool(CanvasPanel canvas, Color strokeColor, int strokeWidth) {
        this.canvas = canvas;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        currentPath = new FreeDrawPath(
            e.getX(), e.getY(),
            0, 0,
            null, // fillColor 없음
            ColorManager.getInstance().getCurrentColor(),
            strokeWidth
        );
        currentPath.addPoint(e.getPoint());
        canvas.addShape(currentPath);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (currentPath != null) {
            currentPath.addPoint(e.getPoint());
            canvas.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (currentPath != null) {
            currentPath.setClosed(true);
            currentPath = null;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void draw(Graphics g) {
    }

    @Override
    public void activate() {
        active = true;
    }

    @Override
    public void deactivate() {
        active = false;
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
