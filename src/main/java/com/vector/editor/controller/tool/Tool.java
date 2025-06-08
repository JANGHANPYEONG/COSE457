package com.vector.editor.controller.tool;

import com.vector.editor.model.Document;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;

public interface Tool {
    void mousePressed(MouseEvent e, Document document);
    void mouseDragged(MouseEvent e, Document document);
    void mouseReleased(MouseEvent e, Document document);
    String getName();
    String getIconPath();
    void onKeyPressed(KeyEvent e);
    void onKeyReleased(KeyEvent e);
} 