package com.vector.editor.tools;

import com.vector.editor.command.CommandManager;
import java.util.HashMap;
import java.util.Map;
import com.vector.editor.CanvasPanel;

public class ToolManager {
    private Map<String, Tool> tools;
    private Tool currentTool;
    private CanvasPanel canvas;
    private CommandManager commandManager;
    
    public ToolManager(CanvasPanel canvas, CommandManager commandManager) {
        this.canvas = canvas;
        this.commandManager = commandManager;
        tools = new HashMap<>();
        initializeTools();
    }
    
    private void initializeTools() {
        // 기본 도구들 등록
        registerTool("selection", new SelectionTool(canvas, commandManager));
        registerTool("rectangle", new RectangleTool(canvas));
        registerTool("ellipse", new EllipseTool(canvas));
        registerTool("line", new LineTool(canvas));
        registerTool("text", new TextTool(canvas));
    }
    
    public void registerTool(String name, Tool tool) {
        tools.put(name, tool);
    }
    
    public void setCurrentTool(String toolName) {
        Tool newTool = tools.get(toolName);
        if (newTool != null) {
            if (currentTool != null) {
                currentTool.deactivate();
            }
            currentTool = newTool;
            currentTool.activate();
            canvas.repaint();
        }
    }
    
    public Tool getCurrentTool() {
        return currentTool;
    }
    
    public void deactivateAllTools() {
        if (currentTool != null) {
            currentTool.deactivate();
            currentTool = null;
            canvas.repaint();
        }
    }
} 