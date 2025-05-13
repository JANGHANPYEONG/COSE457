package com.vector.editor.tools;

import com.vector.editor.command.Command;
import com.vector.editor.command.CommandManager;
import com.vector.editor.command.MoveCommand;
import com.vector.editor.command.ResizeCommand;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import com.vector.editor.core.Shape;
import com.vector.editor.CanvasPanel;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.util.Map;

public class SelectionTool implements Tool {
    private enum Mode { NONE, RESIZE, MOVE, SELECTION_BOX }
    private boolean active = false;
    private Point startPoint;
    private Point currentPoint;
    private boolean isDragging = false;
    private CanvasPanel canvas;
    private CommandManager commandManager;
    private final Map<Shape, Rectangle> beforeResizeBounds = new HashMap<>();
    private final Map<Shape, Rectangle> afterResizeBounds = new HashMap<>();
    private final Map<Shape, Point> beforeMoveStates = new HashMap<>();

    // 추가: 리사이즈/이동 상태 관리
    private Mode currentMode = Mode.NONE;
    private Shape resizingShape = null;
    private Shape movingShape = null;
    private Shape.HandlePosition resizingHandle = null;
    private Point prevMousePoint = null;

    public SelectionTool(CanvasPanel canvas, CommandManager commandManager) {
        this.canvas = canvas;
        this.commandManager = commandManager;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!active) return;
        Point p = e.getPoint();
        currentMode = Mode.NONE;
        resizingShape = null;
        movingShape = null;
        resizingHandle = null;
        prevMousePoint = null;

        // 1. 리사이즈 핸들 클릭 여부 확인
        for (Shape shape : canvas.getShapes()) {
            if (!shape.isSelected()) continue;
            for (Map.Entry<Shape.HandlePosition, Rectangle> entry : shape.getResizeHandles().entrySet()) {
                if (entry.getValue().contains(p)) {
                    currentMode = Mode.RESIZE;
                    resizingShape = shape;
                    resizingHandle = entry.getKey();
                    prevMousePoint = p;
                    return;
                }
            }
        }

        // 2. 선택된 도형 내부 클릭(이동)
        for (int i = canvas.getShapes().size() - 1; i >= 0; i--) {
            Shape shape = canvas.getShapes().get(i);
            if (shape.isSelected() && shape.contains(p.x, p.y)) {
                currentMode = Mode.MOVE;
                movingShape = shape;
                prevMousePoint = p;

                for (Shape s : canvas.getShapes()) {
                    if (s.isSelected()) {
                        beforeResizeBounds.put(s, new Rectangle(s.getX(), s.getY(), s.getWidth(), s.getHeight()));
                        beforeMoveStates.put(s, new Point(s.getX(), s.getY()));
                    }
                }
                return;
            }
        }

        // 3. 그 외: 선택 박스
        startPoint = p;
        currentPoint = startPoint;
        isDragging = true;
        currentMode = Mode.SELECTION_BOX;
        if (!e.isShiftDown()) {
            canvas.clearAllSelections();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!active) return;
        Point p = e.getPoint();
        switch (currentMode) {
            case RESIZE:
                afterResizeBounds.clear();

                for (Shape shape : canvas.getSelectedShapes()) {
                    Rectangle current = new Rectangle(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());
                    afterResizeBounds.put(shape, current);
                }

                if (!beforeResizeBounds.isEmpty()) {
                    Command resizeCmd = new ResizeCommand(
                        new ArrayList<>(beforeResizeBounds.keySet()),
                        beforeResizeBounds,
                        afterResizeBounds
                    );
                    commandManager.executeCommand(resizeCmd);
                }

                resizingShape = null;
                resizingHandle = null;
                prevMousePoint = null;
                beforeResizeBounds.clear();
                afterResizeBounds.clear();
                break;
            case MOVE:
                if (!canvas.getSelectedShapes().isEmpty()) {
                    Map<Shape, Point> afterMoveStates = new HashMap<>();

                    for (Shape shape : canvas.getSelectedShapes()) {
                        afterMoveStates.put(shape, new Point(shape.getX(), shape.getY()));
                    }

                    boolean moved = canvas.getSelectedShapes().stream().anyMatch(shape -> {
                        Point before = beforeMoveStates.get(shape);
                        Point after = afterMoveStates.get(shape);
                        return before != null && (before.x != after.x || before.y != after.y);
                    });

                    if (moved) {
                        Command moveCmd = new MoveCommand(
                            new ArrayList<>(canvas.getSelectedShapes()),
                            new HashMap<>(beforeMoveStates),
                            afterMoveStates
                        );
                        commandManager.executeCommand(moveCmd);
                    }
                }

                prevMousePoint = null;
                beforeMoveStates.clear();
                break;
            case SELECTION_BOX:
                currentPoint = p;
                isDragging = false;
                // 드래그 영역 내 도형 선택
                Rectangle selectionRect = new Rectangle(
                    Math.min(startPoint.x, currentPoint.x),
                    Math.min(startPoint.y, currentPoint.y),
                    Math.abs(currentPoint.x - startPoint.x),
                    Math.abs(currentPoint.y - startPoint.y)
                );
                boolean anySelected = false;
                for (Shape shape : canvas.getShapes()) {
                    Rectangle bounds = new Rectangle(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());
                    if (selectionRect.intersects(bounds)) {
                        shape.select();
                        if (!canvas.getSelectedShapes().contains(shape)) {
                            canvas.getSelectedShapes().add(shape);
                        }
                        anySelected = true;
                    }
                }
                // StatePanel 갱신
                if (canvas.getStatePanel() != null) {
                    if (anySelected) {
                        canvas.getStatePanel().updateShapeInfo(canvas.getSelectedShapes().get(0));
                    } else {
                        canvas.getStatePanel().updateShapeInfo(null);
                    }
                }
                canvas.repaint();
                break;
            default:
                break;
        }
        currentMode = Mode.NONE;
        resizingShape = null;
        movingShape = null;
        resizingHandle = null;
        prevMousePoint = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!active) return;
        Point p = e.getPoint();
        switch (currentMode) {
            case RESIZE:
                if (resizingShape != null && resizingHandle != null && prevMousePoint != null) {
                    int dx = p.x - prevMousePoint.x;
                    int dy = p.y - prevMousePoint.y;

                    switch (resizingHandle) {
                        case BOTTOM_RIGHT:
                            resizingShape.resize(dx, dy);
                            break;
                        case TOP_LEFT:
                            resizingShape.move(dx, dy);
                            resizingShape.resize(-dx, -dy);
                            break;
                        case TOP_RIGHT:
                            resizingShape.move(0, dy);
                            resizingShape.resize(dx, -dy);
                            break;
                        case BOTTOM_LEFT:
                            resizingShape.move(dx, 0);
                            resizingShape.resize(-dx, dy);
                            break;
                    }
                    prevMousePoint = p;
                    canvas.repaint();
                    // StatePanel 정보 갱신 (리사이징 중)
                    if (canvas.getStatePanel() != null && resizingShape != null) {
                        canvas.getStatePanel().updateShapeInfo(resizingShape);
                    }
                }
                break;
            case MOVE:
                if (prevMousePoint != null) {
                    int dx = p.x - prevMousePoint.x;
                    int dy = p.y - prevMousePoint.y;
                    for (Shape shape : canvas.getShapes()) {
                        if (shape.isSelected()) {
                            shape.move(dx, dy);
                            // StatePanel 정보 갱신 (이동 중)
                            if (canvas.getStatePanel() != null) {
                                canvas.getStatePanel().updateShapeInfo(shape);
                            }
                        }
                    }
                    prevMousePoint = p;
                    canvas.repaint();
                }
                break;
            case SELECTION_BOX:
                currentPoint = p;
                canvas.repaint();
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!active) return;
        Point clickPoint = e.getPoint();
        boolean isShift = e.isShiftDown();
        Shape clickedShape = null;
        // 가장 위에 있는 도형 찾기
        List<Shape> shapes = canvas.getShapes();
        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i).contains(clickPoint.x, clickPoint.y)) {
                clickedShape = shapes.get(i);
                break;
            }
        }
        if (clickedShape != null) {
            if (clickedShape.isSelected() && isShift) {
                clickedShape.deselect();
                canvas.getSelectedShapes().remove(clickedShape);
            } else {
                if (!isShift) {
                    canvas.clearAllSelections();
                    // StatePanel 정보 갱신 (선택 해제)
                    if (canvas.getStatePanel() != null) {
                        canvas.getStatePanel().updateShapeInfo(null);
                    }
                }
                clickedShape.select();
                if (!canvas.getSelectedShapes().contains(clickedShape)) {
                    canvas.getSelectedShapes().add(clickedShape);
                }
            }
            // StatePanel 정보 갱신
            if (canvas.getStatePanel() != null) {
                canvas.getStatePanel().updateShapeInfo(clickedShape);
            }
        } else if (!isShift) {
            canvas.clearAllSelections();
        }
        canvas.repaint();
    }

    @Override
    public void draw(Graphics g) {
        if (!active || currentMode != Mode.SELECTION_BOX || !isDragging || startPoint == null || currentPoint == null) return;
        int x = Math.min(startPoint.x, currentPoint.x);
        int y = Math.min(startPoint.y, currentPoint.y);
        int width = Math.abs(currentPoint.x - startPoint.x);
        int height = Math.abs(currentPoint.y - startPoint.y);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0, 120, 215, 64));
        g2.fillRect(x, y, width, height);
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(1));
        g2.drawRect(x, y, width, height);
    }

    @Override
    public void activate() {
        active = true;
    }

    @Override
    public void deactivate() {
        active = false;
        isDragging = false;
        currentMode = Mode.NONE;
        resizingShape = null;
        movingShape = null;
        resizingHandle = null;
        prevMousePoint = null;
    }

    @Override
    public boolean isActive() {
        return active;
    }
} 