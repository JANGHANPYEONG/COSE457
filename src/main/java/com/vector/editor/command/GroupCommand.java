package com.vector.editor.command;

import com.vector.editor.model.Document;
import com.vector.editor.model.shape.Shape;
import com.vector.editor.model.shape.GroupShape;
import java.util.List;

public class GroupCommand implements Command {
    private final Document document;
    private final List<Shape> selectedShapes;
    private GroupShape group;

    public GroupCommand(Document document, List<Shape> selectedShapes) {
        this.document = document;
        this.selectedShapes = selectedShapes;
    }

    @Override
    public void execute() {
        if (selectedShapes.size() > 1) {
            // 새로운 그룹 생성
            group = new GroupShape();
            
            // 선택된 모든 도형을 그룹에 추가하고 문서에서 제거
            for (Shape shape : selectedShapes) {
                document.removeShape(shape);
                group.addShape(shape);
            }
            
            // 그룹을 문서에 추가
            document.addShape(group);
            document.selectShape(group);
        }
    }

    @Override
    public void undo() {
        if (group != null) {
            // 그룹에서 모든 도형을 추출
            List<Shape> shapes = group.getShapes();
            
            // 그룹을 문서에서 제거
            document.removeShape(group);
            
            // 개별 도형들을 다시 문서에 추가
            for (Shape shape : shapes) {
                document.addShape(shape);
                document.selectShape(shape);
            }
        }
    }
} 