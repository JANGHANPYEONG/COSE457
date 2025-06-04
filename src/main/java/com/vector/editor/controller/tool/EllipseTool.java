package com.vector.editor.controller.tool;

import com.vector.editor.model.Document;
import com.vector.editor.model.shape.EllipseShape;
import com.vector.editor.model.shape.Shape;
import java.awt.event.MouseEvent;
import com.vector.editor.command.CommandManager;
import com.vector.editor.command.AddShapeCommand;

public class EllipseTool implements Tool {
    private Shape currentShape;
    private int startX, startY;
    private final CommandManager commandManager;

    public EllipseTool(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void mousePressed(MouseEvent e, Document document) {
        startX = e.getX();
        startY = e.getY();
        currentShape = new EllipseShape(
            startX, startY, 0, 0,
            document.getFillColor(),
            document.getStrokeColor(),
            document.getStrokeWidth()
        );
        commandManager.executeCommand(new AddShapeCommand(document, currentShape));
    }

    @Override
    public void mouseDragged(MouseEvent e, Document document) {
        if (currentShape != null) {
            int width = Math.abs(e.getX() - startX);
            int height = Math.abs(e.getY() - startY);
            int x = Math.min(startX, e.getX());
            int y = Math.min(startY, e.getY());
            
            currentShape.setPosition(x, y);
            currentShape.setSize(width, height);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e, Document document) {
        if (currentShape != null) {
            int width = Math.abs(e.getX() - startX);
            int height = Math.abs(e.getY() - startY);
            
            if (width < 5 || height < 5) {
                document.removeShape(currentShape);
            }
            
            currentShape = null;
        }
    }

    @Override
    public String getName() {
        return "Ellipse";
    }

    @Override
    public String getIconPath() {
        return "/icons/ellipse.png";
    }

    @Override
    public void onKeyPressed(java.awt.event.KeyEvent e) {}

    @Override
    public void onKeyReleased(java.awt.event.KeyEvent e) {}
} 