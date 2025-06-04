package com.vector.editor.command;

import java.util.ArrayDeque;
import java.util.Deque;

public class CommandManager {
    private Deque<Command> undoStack;
    private Deque<Command> redoStack;

    public CommandManager() {
        undoStack = new ArrayDeque<>();
        redoStack = new ArrayDeque<>();
    }

    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
} 