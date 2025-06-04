package com.vector.editor.controller;

import com.vector.editor.model.Document;
import com.vector.editor.controller.tool.*;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import com.vector.editor.command.CommandManager;

public class ToolManager {
    private Map<String, Tool> tools;
    private Tool currentTool;
    private Document document;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private CommandManager commandManager;

    public ToolManager(Document document, CommandManager commandManager) {
        this.document = document;
        this.commandManager = commandManager;
        this.tools = new HashMap<>();
        initializeTools();
    }

    private void initializeTools() {
        tools.put("select", new SelectTool(commandManager));
        tools.put("rectangle", new RectangleTool(commandManager));
        tools.put("ellipse", new EllipseTool(commandManager));
        tools.put("line", new LineTool(commandManager));
        tools.put("text", new TextTool(commandManager));
        tools.put("freedraw", new FreeDrawTool(commandManager));
        tools.put("image", new ImageTool(commandManager));
        
        // 기본 도구 설정
        currentTool = tools.get("select");
    }

    public void setCurrentTool(String toolName) {
        Tool oldTool = this.currentTool;
        Tool tool = tools.get(toolName);
        if (tool != null) {
            currentTool = tool;
            pcs.firePropertyChange("currentTool", oldTool, this.currentTool);
        }
    }

    public Tool getCurrentTool() {
        return currentTool;
    }

    public void mousePressed(MouseEvent e) {
        if (currentTool != null) {
            currentTool.mousePressed(e, document);
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (currentTool != null) {
            currentTool.mouseDragged(e, document);
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (currentTool != null) {
            currentTool.mouseReleased(e, document);
        }
    }

    public Map<String, Tool> getTools() {
        return tools;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
} 