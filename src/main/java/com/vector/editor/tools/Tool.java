package com.vector.editor.tools;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;

public interface Tool {
    void mousePressed(MouseEvent e);
    void mouseReleased(MouseEvent e);
    void mouseDragged(MouseEvent e);
    void mouseClicked(MouseEvent e);
    void draw(Graphics g);
    
    // Tool의 상태를 관리하기 위한 메서드
    void activate();
    void deactivate();
    boolean isActive();
    void keyPressed(KeyEvent e);
} 