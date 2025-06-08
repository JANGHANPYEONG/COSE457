package com.vector.editor.controller.tool;

import com.vector.editor.model.Document;
import com.vector.editor.model.shape.FreeDrawPath;
import com.vector.editor.model.shape.Shape;
import java.awt.Point;
import java.awt.event.MouseEvent;
import com.vector.editor.command.CommandManager;
import com.vector.editor.command.AddShapeCommand;

public class FreeDrawTool implements Tool {
    private Shape currentShape;
    private Point lastPoint;
    private final CommandManager commandManager;

    public FreeDrawTool(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void mousePressed(MouseEvent e, Document document) {
        lastPoint = e.getPoint();
        currentShape = new FreeDrawPath(
            document.getStrokeColor(),
            document.getStrokeWidth()
        );
        ((FreeDrawPath)currentShape).addPoint(lastPoint);
        commandManager.executeCommand(new AddShapeCommand(document, currentShape));
    }

    @Override
    public void mouseDragged(MouseEvent e, Document document) {
        if (currentShape != null) {
            Point currentPoint = e.getPoint();
            ((FreeDrawPath)currentShape).addPoint(currentPoint);
            lastPoint = currentPoint;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e, Document document) {
        if (currentShape != null) {
            Point currentPoint = e.getPoint();
            ((FreeDrawPath)currentShape).addPoint(currentPoint);
            currentShape = null;
        }
    }

    @Override
    public String getName() {
        return "Free Draw";
    }

    @Override
    public String getIconPath() {
        return "/icons/freedraw.png";
    }

    @Override
    public void onKeyPressed(java.awt.event.KeyEvent e) {}

    @Override
    public void onKeyReleased(java.awt.event.KeyEvent e) {}
} 