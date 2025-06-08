package com.vector.editor.controller;

import com.vector.editor.model.Document;
import com.vector.editor.model.shape.Shape;
import com.vector.editor.command.Command;
import com.vector.editor.command.CommandManager;
import com.vector.editor.command.ZOrderCommand;
import java.util.List;

public class ZOrderController {
    private final Document document;
    private final CommandManager commandManager;

    public ZOrderController(Document document, CommandManager commandManager) {
        this.document = document;
        this.commandManager = commandManager;
    }

    public void bringToFront(List<Shape> shapes) {
        if (!shapes.isEmpty()) {
            Command command = new ZOrderCommand(document, shapes, ZOrderCommand.ZOrderType.BRING_TO_FRONT);
            commandManager.executeCommand(command);
        }
    }

    public void sendToBack(List<Shape> shapes) {
        if (!shapes.isEmpty()) {
            Command command = new ZOrderCommand(document, shapes, ZOrderCommand.ZOrderType.SEND_TO_BACK);
            commandManager.executeCommand(command);
        }
    }

    public void bringForward(List<Shape> shapes) {
        if (!shapes.isEmpty()) {
            Command command = new ZOrderCommand(document, shapes, ZOrderCommand.ZOrderType.BRING_FORWARD);
            commandManager.executeCommand(command);
        }
    }

    public void sendBackward(List<Shape> shapes) {
        if (!shapes.isEmpty()) {
            Command command = new ZOrderCommand(document, shapes, ZOrderCommand.ZOrderType.SEND_BACKWARD);
            commandManager.executeCommand(command);
        }
    }
} 