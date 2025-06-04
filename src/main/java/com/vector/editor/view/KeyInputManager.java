package com.vector.editor.view;

import com.vector.editor.controller.ToolManager;
import com.vector.editor.command.CommandManager;
import com.vector.editor.command.RemoveShapeCommand;
import com.vector.editor.model.Document;
import com.vector.editor.model.shape.Shape;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class KeyInputManager implements KeyListener {
    private final ToolManager toolManager;
    private final CommandManager commandManager;
    private final Document document;
    private final JComponent repaintTarget;

    public KeyInputManager(ToolManager toolManager, CommandManager commandManager, Document document, JComponent repaintTarget) {
        this.toolManager = toolManager;
        this.commandManager = commandManager;
        this.document = document;
        this.repaintTarget = repaintTarget;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // 글로벌 단축키
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) {
            commandManager.undo();
            repaintTarget.repaint();
            return;
        }
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Y) {
            commandManager.redo();
            repaintTarget.repaint();
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            List<Shape> selected = new ArrayList<>(document.getSelectedShapes());
            for (Shape shape : selected) {
                commandManager.executeCommand(new RemoveShapeCommand(document, shape));
            }
            repaintTarget.repaint();
            return;
        }
        // 나머지 키 입력은 현재 Tool에 위임
        if (toolManager.getCurrentTool() != null) {
            toolManager.getCurrentTool().onKeyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (toolManager.getCurrentTool() != null) {
            toolManager.getCurrentTool().onKeyReleased(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // 필요시 구현
    }

    public void registerGlobalKeyBindings(JFrame frame) {
        JComponent root = frame.getRootPane();
        // Undo (Ctrl+Z)
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Z"), "undo");
        root.getActionMap().put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                commandManager.undo();
                repaintTarget.repaint();
            }
        });
        // Redo (Ctrl+Y)
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Y"), "redo");
        root.getActionMap().put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                commandManager.redo();
                repaintTarget.repaint();
            }
        });
        // Delete
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"), "delete");
        root.getActionMap().put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                java.util.List<Shape> selected = new java.util.ArrayList<>(document.getSelectedShapes());
                for (Shape shape : selected) {
                    commandManager.executeCommand(new RemoveShapeCommand(document, shape));
                }
                repaintTarget.repaint();
            }
        });
    }
} 