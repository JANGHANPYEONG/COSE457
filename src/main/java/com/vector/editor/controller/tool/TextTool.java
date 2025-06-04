package com.vector.editor.controller.tool;

import com.vector.editor.model.Document;
import com.vector.editor.model.shape.TextShape;
import com.vector.editor.model.shape.Shape;
import java.awt.Font;
import java.awt.event.MouseEvent;
import com.vector.editor.command.CommandManager;
import com.vector.editor.command.AddShapeCommand;

public class TextTool implements Tool {
    private Shape currentShape;
    private final CommandManager commandManager;

    public TextTool(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void mousePressed(MouseEvent e, Document document) {
        currentShape = new TextShape(
            e.getX(), e.getY(),
            "New Text",
            new Font("Arial", Font.PLAIN, 12),
            document.getStrokeColor()
        );
        commandManager.executeCommand(new AddShapeCommand(document, currentShape));
    }

    @Override
    public void mouseDragged(MouseEvent e, Document document) {
        // 텍스트 도구는 드래그를 사용하지 않음
    }

    @Override
    public void mouseReleased(MouseEvent e, Document document) {
        // 텍스트 도구는 마우스 릴리즈를 사용하지 않음
    }

    @Override
    public String getName() {
        return "Text";
    }

    @Override
    public String getIconPath() {
        return "/icons/text.png";
    }

    @Override
    public void onKeyPressed(java.awt.event.KeyEvent e) {}

    @Override
    public void onKeyReleased(java.awt.event.KeyEvent e) {}
} 