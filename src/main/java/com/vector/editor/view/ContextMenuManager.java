package com.vector.editor.view;

import com.vector.editor.model.Document;
import com.vector.editor.model.shape.Shape;
import com.vector.editor.model.shape.GroupShape;
import com.vector.editor.command.Command;
import com.vector.editor.command.CommandManager;
import com.vector.editor.command.GroupCommand;
import com.vector.editor.command.UngroupCommand;
import com.vector.editor.controller.ZOrderController;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ContextMenuManager {
    private final Document document;
    private final JPopupMenu contextMenu;
    private final CanvasView canvasView;
    private final CommandManager commandManager;
    private final ZOrderController zOrderController;

    public ContextMenuManager(Document document, CanvasView canvasView, CommandManager commandManager) {
        this.document = document;
        this.canvasView = canvasView;
        this.commandManager = commandManager;
        this.zOrderController = new ZOrderController(document, commandManager);
        this.contextMenu = createContextMenu();
    }

    private JPopupMenu createContextMenu() {
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem groupItem = new JMenuItem("도형 묶기");
        JMenuItem ungroupItem = new JMenuItem("도형 묶기 해제");
        JMenuItem bringToFrontItem = new JMenuItem("맨 앞으로");
        JMenuItem sendToBackItem = new JMenuItem("맨 뒤로");
        JMenuItem bringForwardItem = new JMenuItem("앞으로");
        JMenuItem sendBackwardItem = new JMenuItem("뒤로");

        groupItem.addActionListener(e -> {
            List<Shape> selectedShapes = document.getSelectedShapes();
            if (selectedShapes.size() > 1) {
                Command groupCommand = new GroupCommand(document, selectedShapes);
                commandManager.executeCommand(groupCommand);
            }
        });

        ungroupItem.addActionListener(e -> {
            List<Shape> selectedShapes = document.getSelectedShapes();
            if (selectedShapes.size() == 1 && selectedShapes.get(0) instanceof GroupShape) {
                GroupShape group = (GroupShape) selectedShapes.get(0);
                Command ungroupCommand = new UngroupCommand(document, group);
                commandManager.executeCommand(ungroupCommand);
            }
        });

        bringToFrontItem.addActionListener(e -> {
            List<Shape> selectedShapes = document.getSelectedShapes();
            zOrderController.bringToFront(selectedShapes);
        });

        sendToBackItem.addActionListener(e -> {
            List<Shape> selectedShapes = document.getSelectedShapes();
            zOrderController.sendToBack(selectedShapes);
        });

        bringForwardItem.addActionListener(e -> {
            List<Shape> selectedShapes = document.getSelectedShapes();
            zOrderController.bringForward(selectedShapes);
        });

        sendBackwardItem.addActionListener(e -> {
            List<Shape> selectedShapes = document.getSelectedShapes();
            zOrderController.sendBackward(selectedShapes);
        });

        menu.add(groupItem);
        menu.add(ungroupItem);
        menu.addSeparator();
        menu.add(bringToFrontItem);
        menu.add(sendToBackItem);
        menu.add(bringForwardItem);
        menu.add(sendBackwardItem);

        return menu;
    }

    public void showContextMenu(Component invoker, int x, int y) {
        List<Shape> selectedShapes = document.getSelectedShapes();
        boolean hasSelection = !selectedShapes.isEmpty();
        
        // Enable/disable menu items based on selection
        contextMenu.getComponent(0).setEnabled(selectedShapes.size() > 1); // Group requires multiple shapes
        contextMenu.getComponent(1).setEnabled(hasSelection && selectedShapes.size() == 1 && selectedShapes.get(0) instanceof GroupShape); // Ungroup requires single group
        contextMenu.getComponent(3).setEnabled(hasSelection); // Bring to front
        contextMenu.getComponent(4).setEnabled(hasSelection); // Send to back
        contextMenu.getComponent(5).setEnabled(hasSelection); // Bring forward
        contextMenu.getComponent(6).setEnabled(hasSelection); // Send backward
        
        // Show context menu
        contextMenu.show(invoker, x, y);
    }
} 