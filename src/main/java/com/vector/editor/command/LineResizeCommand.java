package com.vector.editor.command;

import com.vector.editor.model.shape.LineShape;
import java.awt.Point;

public class LineResizeCommand implements Command {
    private final LineShape line;
    private final Point beforeStart, beforeEnd;
    private final Point afterStart, afterEnd;

    public LineResizeCommand(LineShape line, Point beforeStart, Point beforeEnd, Point afterStart, Point afterEnd) {
        this.line = line;
        this.beforeStart = beforeStart;
        this.beforeEnd = beforeEnd;
        this.afterStart = afterStart;
        this.afterEnd = afterEnd;
    }

    @Override
    public void execute() {
        line.setPosition(afterStart.x, afterStart.y);
        line.setEndPoint(afterEnd.x, afterEnd.y);
    }

    @Override
    public void undo() {
        line.setPosition(beforeStart.x, beforeStart.y);
        line.setEndPoint(beforeEnd.x, beforeEnd.y);
    }
} 