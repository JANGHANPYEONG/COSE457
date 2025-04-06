package com.vector.editor.tools;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

public interface ToolState {
    void mousePressed(MouseEvent e);
    void mouseReleased(MouseEvent e);
    void mouseDragged(MouseEvent e);
    void mouseClicked(MouseEvent e);
    void draw(Graphics g);
} 