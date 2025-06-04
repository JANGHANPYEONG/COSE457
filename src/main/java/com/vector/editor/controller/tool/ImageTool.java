package com.vector.editor.controller.tool;

import com.vector.editor.model.Document;
import com.vector.editor.model.shape.ImageShape;
import com.vector.editor.model.shape.Shape;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.vector.editor.command.CommandManager;
import com.vector.editor.command.AddShapeCommand;

public class ImageTool implements Tool {
    private Shape currentShape;
    private int startX, startY;
    private final CommandManager commandManager;

    public ImageTool(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void mousePressed(MouseEvent e, Document document) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg", "gif"));
        
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                startX = e.getX();
                startY = e.getY();
                currentShape = new ImageShape(startX, startY, selectedFile);
                commandManager.executeCommand(new AddShapeCommand(document, currentShape));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
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
        return "Image";
    }

    @Override
    public String getIconPath() {
        return "/icons/image.png";
    }

    @Override
    public void onKeyPressed(java.awt.event.KeyEvent e) {}

    @Override
    public void onKeyReleased(java.awt.event.KeyEvent e) {}
} 