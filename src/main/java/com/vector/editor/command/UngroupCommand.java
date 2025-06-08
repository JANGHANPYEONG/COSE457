package com.vector.editor.command;

import com.vector.editor.model.Document;
import com.vector.editor.model.shape.Shape;
import com.vector.editor.model.shape.GroupShape;
import java.util.List;

public class UngroupCommand implements Command {
    private final Document document;
    private final GroupShape group;
    private List<Shape> ungroupedShapes;

    public UngroupCommand(Document document, GroupShape group) {
        this.document = document;
        this.group = group;
    }

    @Override
    public void execute() {
        if (group != null) {
            // 그룹에서 모든 도형을 추출
            ungroupedShapes = group.getShapes();
            
            // 그룹을 문서에서 제거
            document.removeShape(group);
            
            // 개별 도형들을 다시 문서에 추가
            for (Shape shape : ungroupedShapes) {
                document.addShape(shape);
                document.selectShape(shape);
            }
        }
    }

    @Override
    public void undo() {
        if (ungroupedShapes != null) {
            // 개별 도형들을 문서에서 제거
            for (Shape shape : ungroupedShapes) {
                document.removeShape(shape);
                group.addShape(shape);
            }
            
            // 그룹을 다시 문서에 추가
            document.addShape(group);
            document.selectShape(group);
        }
    }
} 