package com.vector.editor.command;

import com.vector.editor.model.Document;
import com.vector.editor.model.shape.Shape;
import java.util.List;
import java.util.ArrayList;

public class ZOrderCommand implements Command {
    public enum ZOrderType {
        BRING_TO_FRONT,
        SEND_TO_BACK,
        BRING_FORWARD,
        SEND_BACKWARD
    }

    private final Document document;
    private final List<Shape> shapes;
    private final ZOrderType type;
    private List<Integer> originalZOrders;

    public ZOrderCommand(Document document, List<Shape> shapes, ZOrderType type) {
        this.document = document;
        this.shapes = shapes;
        this.type = type;
        this.originalZOrders = new ArrayList<>();
        // 원래 zOrder 값들을 저장
        for (Shape shape : shapes) {
            originalZOrders.add(shape.getZOrder());
        }
    }

    @Override
    public void execute() {
        switch (type) {
            case BRING_TO_FRONT:
                document.bringToFront(shapes);
                break;
            case SEND_TO_BACK:
                document.sendToBack(shapes);
                break;
            case BRING_FORWARD:
                document.bringForward(shapes);
                break;
            case SEND_BACKWARD:
                document.sendBackward(shapes);
                break;
        }
    }

    @Override
    public void undo() {
        // 원래 zOrder 값으로 복원
        for (int i = 0; i < shapes.size(); i++) {
            shapes.get(i).setZOrder(originalZOrders.get(i));
        }
    }
} 