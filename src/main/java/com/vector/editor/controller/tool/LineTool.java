package com.vector.editor.controller.tool;

import com.vector.editor.model.Document;
import com.vector.editor.model.shape.LineShape;
import com.vector.editor.model.shape.Shape;
import java.awt.event.MouseEvent;
import com.vector.editor.command.CommandManager;
import com.vector.editor.command.AddShapeCommand;

public class LineTool implements Tool {
    private Shape currentShape;
    private int startX, startY;
    private final CommandManager commandManager;

    public LineTool(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void mousePressed(MouseEvent e, Document document) {
        startX = e.getX();
        startY = e.getY();
        currentShape = new LineShape(
            startX, startY, startX, startY,
            document.getStrokeColor(),
            document.getStrokeWidth()
        );
        commandManager.executeCommand(new AddShapeCommand(document, currentShape));
    }

    @Override
    public void mouseDragged(MouseEvent e, Document document) {
        if (currentShape != null) {
            ((LineShape)currentShape).setEndPoint(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e, Document document) {
        if (currentShape != null) {
            int endX = e.getX();
            int endY = e.getY();
            
            if (Math.abs(endX - startX) < 5 && Math.abs(endY - startY) < 5) {
                document.removeShape(currentShape);
            }
            
            currentShape = null;
        }
    }

    @Override
    public String getName() {
        return "Line";
    }

    @Override
    public String getIconPath() {
        return "/icons/line.png";
    }

    @Override
    public void onKeyPressed(java.awt.event.KeyEvent e) {}

    @Override
    public void onKeyReleased(java.awt.event.KeyEvent e) {}
} 