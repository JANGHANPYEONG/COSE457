package com.vector.editor.controller.tool;

import com.vector.editor.model.Document;
import com.vector.editor.model.shape.Shape;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import com.vector.editor.command.CommandManager;
import com.vector.editor.model.shape.LineShape;
import com.vector.editor.command.ResizeCommand;
import com.vector.editor.command.LineResizeCommand;
import com.vector.editor.command.MoveCommand;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class SelectTool implements Tool {
    private Point startPoint;
    private Shape selectedShape;
    private enum Mode { NONE, MOVE, RESIZE }
    private Mode mode = Mode.NONE;
    private int resizeHandleIndex = -1;
    private Point selectionBoxStart = null;
    private Point selectionBoxEnd = null;
    private boolean isSelectionBoxMode = false;
    private final CommandManager commandManager;
    private Map<Shape, Rectangle> beforeResize = new HashMap<>();
    private Map<Shape, Point[]> beforeLineResize = new HashMap<>();
    private Map<Shape, Point> beforeMove = new HashMap<>();

    public SelectTool(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public Rectangle getSelectionBox() {
        if (selectionBoxStart != null && selectionBoxEnd != null) {
            int x = Math.min(selectionBoxStart.x, selectionBoxEnd.x);
            int y = Math.min(selectionBoxStart.y, selectionBoxEnd.y);
            int w = Math.abs(selectionBoxStart.x - selectionBoxEnd.x);
            int h = Math.abs(selectionBoxStart.y - selectionBoxEnd.y);
            return new Rectangle(x, y, w, h);
        }
        return null;
    }

    private boolean lineIntersectsBox(Shape shape, Rectangle box) {
        if (shape instanceof com.vector.editor.model.shape.LineShape) {
            com.vector.editor.model.shape.LineShape line = (com.vector.editor.model.shape.LineShape) shape;
            int x1 = line.getX(), y1 = line.getY(), x2 = line.getEndX(), y2 = line.getEndY();
            return box.intersectsLine(x1, y1, x2, y2);
        }
        return false;
    }

    @Override
    public void mousePressed(MouseEvent e, Document document) {
        Point point = e.getPoint();
        Shape hitShape = null;
        int handleIdx = -1;
        // 1. 핸들 hit test 우선
        for (Shape shape : document.getShapes()) {
            if (shape.isSelected()) {
                int idx = shape.getHandleAt(point.x, point.y);
                if (idx != -1) {
                    hitShape = shape;
                    handleIdx = idx;
                    // 핸들을 클릭한 도형만 선택 상태로 변경
                    document.clearSelection();
                    document.selectShape(shape);
                    break;
                }
            }
        }
        if (hitShape != null) {
            selectedShape = hitShape;
            startPoint = point;
            resizeHandleIndex = handleIdx;
            mode = Mode.RESIZE;
            isSelectionBoxMode = false;
            // 리사이즈 전 상태 저장
            beforeResize.clear();
            beforeLineResize.clear();
            if (hitShape instanceof LineShape) {
                LineShape l = (LineShape) hitShape;
                beforeLineResize.put(l, new Point[]{new Point(l.getX(), l.getY()), new Point(l.getEndX(), l.getEndY())});
            } else {
                beforeResize.put(hitShape, new Rectangle(hitShape.getX(), hitShape.getY(), hitShape.getWidth(), hitShape.getHeight()));
            }
        } else {
            // 2. 도형 내부 hit test
            Shape shape = document.findShapeAt(point.x, point.y);
            if (shape != null) {
                if (e.isShiftDown()) {
                    if (document.isSelected(shape)) {
                        document.deselectShape(shape);
                    } else {
                        document.selectShape(shape);
                    }
                } else {
                    // 선택된 도형 중 하나를 클릭한 경우
                    if (document.isSelected(shape)) {
                        // 이미 선택된 도형을 클릭한 경우, 모든 선택된 도형을 이동
                        selectedShape = shape;
                        startPoint = point;
                        mode = Mode.MOVE;
                        // 이동 전 상태 저장
                        beforeMove.clear();
                        for (Shape s : document.getSelectedShapes()) {
                            beforeMove.put(s, new Point(s.getX(), s.getY()));
                        }
                    } else {
                        // 새로운 도형을 클릭한 경우, 이전 선택을 모두 해제하고 새로 선택
                        document.clearSelection();
                        document.selectShape(shape);
                        selectedShape = shape;
                        startPoint = point;
                        mode = Mode.MOVE;
                        // 이동 전 상태 저장
                        beforeMove.clear();
                        beforeMove.put(shape, new Point(shape.getX(), shape.getY()));
                    }
                }
                isSelectionBoxMode = false;
            } else {
                // 도형이 없는 곳에서 드래그 시작: selection box 모드
                document.clearSelection();
                selectedShape = null;
                mode = Mode.NONE;
                isSelectionBoxMode = true;
                selectionBoxStart = point;
                selectionBoxEnd = point;
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e, Document document) {
        if (mode == Mode.MOVE && !document.getSelectedShapes().isEmpty()) {
            int dx = e.getX() - startPoint.x;
            int dy = e.getY() - startPoint.y;
            for (Shape s : document.getSelectedShapes()) {
                s.move(dx, dy);
            }
            startPoint = e.getPoint();
        } else if (selectedShape != null && mode == Mode.RESIZE) {
            selectedShape.resize(resizeHandleIndex, e.getX(), e.getY());
            startPoint = e.getPoint();
        } else if (isSelectionBoxMode && selectionBoxStart != null) {
            selectionBoxEnd = e.getPoint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e, Document document) {
        if (mode == Mode.MOVE && !document.getSelectedShapes().isEmpty()) {
            // 이동 후 상태 저장 및 커맨드 실행
            Map<Shape, Point> afterMove = new HashMap<>();
            for (Shape s : document.getSelectedShapes()) {
                afterMove.put(s, new Point(s.getX(), s.getY()));
            }
            commandManager.executeCommand(new MoveCommand(document, new ArrayList<>(document.getSelectedShapes()), beforeMove, afterMove));
        } else if (mode == Mode.RESIZE && selectedShape != null) {
            // 리사이즈 후 상태 저장 및 커맨드 실행
            Map<Shape, Rectangle> afterResize = new HashMap<>();
            Map<LineShape, Point[]> afterLineResize = new HashMap<>();
            for (Shape s : document.getSelectedShapes()) {
                if (s instanceof LineShape) {
                    LineShape l = (LineShape) s;
                    afterLineResize.put(l, new Point[]{new Point(l.getX(), l.getY()), new Point(l.getEndX(), l.getEndY())});
                } else {
                    afterResize.put(s, new Rectangle(s.getX(), s.getY(), s.getWidth(), s.getHeight()));
                }
            }
            // Ellipse 등 일반 도형
            if (!afterResize.isEmpty()) {
                commandManager.executeCommand(new ResizeCommand(document, List.copyOf(afterResize.keySet()), beforeResize, afterResize));
            }
            // Line 도형
            for (LineShape l : afterLineResize.keySet()) {
                Point[] before = beforeLineResize.get(l);
                Point[] after = afterLineResize.get(l);
                if (before != null && after != null) {
                    commandManager.executeCommand(new LineResizeCommand(l, before[0], before[1], after[0], after[1]));
                }
            }
        }
        if (isSelectionBoxMode && selectionBoxStart != null && selectionBoxEnd != null) {
            Rectangle selBox = getSelectionBox();
            for (Shape shape : document.getShapes()) {
                if (selBox.intersects(shape.getBounds()) || lineIntersectsBox(shape, selBox)) {
                    document.selectShape(shape);
                }
            }
        }
        mode = Mode.NONE;
        resizeHandleIndex = -1;
        isSelectionBoxMode = false;
        selectionBoxStart = null;
        selectionBoxEnd = null;
    }

    @Override
    public String getName() {
        return "Select";
    }

    @Override
    public String getIconPath() {
        return "/icons/select.png";
    }

    @Override
    public void onKeyPressed(java.awt.event.KeyEvent e) {}
    @Override
    public void onKeyReleased(java.awt.event.KeyEvent e) {}
} 