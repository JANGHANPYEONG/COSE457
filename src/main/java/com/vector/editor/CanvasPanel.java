package com.vector.editor;

import com.vector.editor.command.AddShapeCommand;
import com.vector.editor.command.Command;
import com.vector.editor.command.CommandManager;
import com.vector.editor.command.MoveCommand;
import com.vector.editor.command.PropertyChangeCommand;
import com.vector.editor.command.ResizeCommand;
import com.vector.editor.core.Shape;
import com.vector.editor.core.Shape.HandlePosition;
import com.vector.editor.core.ShapeObserver;
import com.vector.editor.shapes.GroupShape;
import com.vector.editor.shapes.TextShape;
import com.vector.editor.tools.Tool;
import com.vector.editor.tools.ToolManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class CanvasPanel extends JPanel implements ShapeObserver {
    private final CommandManager commandManager;
    private final ToolManager toolManager;

    private static final Color BACKGROUND_COLOR = Color.WHITE;

    private List<Shape> shapes = new ArrayList<>();
    private Shape selectedShape = null;
    private List<Shape> selectedShapes = new ArrayList<>();
    private JTextField textEditor = null;

    private Point dragStart = null;
    private Point dragEnd = null;
    private boolean isRubberBandActive = false;

    private boolean isDraggingShapes = false;
    private Shape resizingShape = null;
    private HandlePosition resizingHandle = null;
    private Point prevMousePoint = null;

    // 도형 원래 위치 및 크기 저장 (Undo/Redo용)
    private final Map<Shape, Rectangle> beforeResizeBounds = new HashMap<>();
    private final Map<Shape, Rectangle> afterResizeBounds = new HashMap<>();

    private final Map<Shape, Point> beforeMoveStates = new HashMap<>();
    private StatePanel statePanel;

    public CanvasPanel(CommandManager commandManager) {
        this.commandManager = commandManager;
        this.toolManager = new ToolManager(this);

        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(800, 600));

        // Add mouse listeners for drawing
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (statePanel != null) {
                    statePanel.updateMousePosition(e.getX(), e.getY());
                }
                Point p = e.getPoint();

                // 현재 도구가 활성화되어 있으면 도구에 마우스 이벤트 위임
                Tool currentTool = toolManager.getCurrentTool();
                if (currentTool != null && currentTool.isActive()) {
                    currentTool.mousePressed(e);
                    return;
                }

                // 리사이징 핸들 확인
                Shape shapeToResize = checkForResizeHandles(p);
                if (shapeToResize != null) {
                    return; // 리사이징 핸들을 찾았으면 여기서 종료
                }

                // 도형 선택 또는 드래그 확인
                Shape clickedShape = findShapeAt(p);
                if (clickedShape != null) {
                    handleShapeClick(clickedShape, e);
                    return; // 도형을 찾았으면 여기서 종료
                }

                // 빈 공간 클릭 - 다중 선택을 위한 러버밴드 시작 또는 모든 선택 해제
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (!e.isShiftDown()) {
                        // 모든 선택 해제
                        clearAllSelections();
                    }

                    // 러버밴드 시작
                    dragStart = e.getPoint();
                    dragEnd = e.getPoint();
                    isRubberBandActive = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Tool tool = toolManager.getCurrentTool();
                if (tool != null && tool.isActive()) {
                    tool.mouseReleased(e);
                    return;
                }

                // 리사이징 종료 처리
                if (resizingShape != null) {
                    finalizeResizing();
                    return;
                }

                // 드래깅 종료 처리
                if (isDraggingShapes) {
                    finalizeDragging();
                    return;
                }

                // 러버밴드 선택 종료 처리
                if (isRubberBandActive) {
                    finalizeRubberBandSelection(e);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (statePanel != null) {
                    statePanel.updateMousePosition(e.getX(), e.getY());
                }
                Tool tool = toolManager.getCurrentTool();
                if (tool != null && tool.isActive()) {
                    tool.mouseClicked(e);
                    return;
                }

                // 텍스트 도형 더블 클릭 시 편집기 표시
                if (e.getClickCount() == 2) {
                    Shape clickedShape = findShapeAt(e.getPoint());
                    if (clickedShape instanceof TextShape) {
                        showInlineTextEditor((TextShape) clickedShape);
                    }
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (statePanel != null) {
                    statePanel.updateMousePosition(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (statePanel != null) {
                    statePanel.updateMousePosition(e.getX(), e.getY());
                }
                Tool tool = toolManager.getCurrentTool();
                if (tool != null && tool.isActive()) {
                    tool.mouseDragged(e);
                    return;
                }

                Point curr = e.getPoint();

                // 리사이징 중인 경우
                if (resizingShape != null && resizingHandle != null && prevMousePoint != null) {
                    handleResizeDrag(curr);
                    return;
                }

                // 도형을 드래그하는 경우
                if (isDraggingShapes && prevMousePoint != null && !selectedShapes.isEmpty()) {
                    handleShapeDrag(curr);
                    return;
                }

                // 러버밴드 드래그
                if (isRubberBandActive) {
                    dragEnd = curr;
                    repaint();
                }
            }
        });
    }

    // 리사이징 핸들 확인
    private Shape checkForResizeHandles(Point p) {
        for (Shape shape : selectedShapes) {
            for (Map.Entry<HandlePosition, Rectangle> entry : shape.getResizeHandles().entrySet()) {
                if (entry.getValue().contains(p)) {
                    resizingShape = shape;
                    resizingHandle = entry.getKey();
                    prevMousePoint = p;

                    // 원래 크기 저장
                    saveBeforeResizeBounds(shape);

                    return shape;
                }
            }
        }
        return null;
    }

    private void saveBeforeResizeBounds(Shape shape) {
        beforeResizeBounds.put(shape, new Rectangle(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight()));
    }

    // 특정 위치에 있는 도형 찾기
    private Shape findShapeAt(Point p) {
        // 역순으로 검색해서 가장 위에 있는(마지막으로 추가된) 도형을 찾음
        for (int i = shapes.size() - 1; i >= 0; i--) {
            Shape shape = shapes.get(i);
            if (shape.contains(p.x, p.y)) {
                return shape;
            }
        }
        return null;
    }

    // 도형 클릭 처리
    private void handleShapeClick(Shape clickedShape, MouseEvent e) {
        if (!e.isShiftDown()) {
            // Shift 키가 눌려있지 않으면 모든 선택 해제
            for (Shape shape : shapes) {
                shape.deselect();
            }
            selectedShapes.clear();
        }

        // 클릭한 도형 선택
        clickedShape.select();
        if (!selectedShapes.contains(clickedShape)) {
            selectedShapes.add(clickedShape);
        }
        selectedShape = clickedShape;

        // StatePanel 업데이트
        if (statePanel != null) {
            statePanel.updateShapeInfo(clickedShape);
        }

        // 드래그 시작 지점 저장
        dragStart = e.getPoint();
        prevMousePoint = e.getPoint();
        isDraggingShapes = true;

        // 모든 선택된 도형의 원래 위치 저장
        for (Shape shape : selectedShapes) {
            beforeMoveStates.put(shape, new Point(shape.getX(), shape.getY()));
        }

        repaint();
    }

    // 모든 선택 해제
    public void clearAllSelections() {
        for (Shape s : shapes) {
            s.deselect();
        }
        selectedShapes.clear();
        selectedShape = null;
        removeInlineTextEditor();
        repaint();
    }

    // 리사이징 드래그 처리
    private void handleResizeDrag(Point curr) {
        int dx = curr.x - prevMousePoint.x;
        int dy = curr.y - prevMousePoint.y;

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

        prevMousePoint = curr;
        repaint();
    }

    // 도형 드래그 처리
    private void handleShapeDrag(Point curr) {
        int dx = curr.x - prevMousePoint.x;
        int dy = curr.y - prevMousePoint.y;

        // 선택된 모든 도형 이동
        for (Shape shape : selectedShapes) {
            shape.move(dx, dy);
        }

        prevMousePoint = curr;
        repaint();
    }

    // 리사이징 완료 처리
    private void finalizeResizing() {
        afterResizeBounds.clear();

        for (Shape shape : selectedShapes) {
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
        repaint();
    }

    // 드래깅 완료 처리
    private void finalizeDragging() {
        if (!selectedShapes.isEmpty()) {
            Map<Shape, Point> afterMoveStates = new HashMap<>();

            for (Shape shape : selectedShapes) {
                afterMoveStates.put(shape, new Point(shape.getX(), shape.getY()));
            }

            boolean moved = selectedShapes.stream().anyMatch(shape -> {
                Point before = beforeMoveStates.get(shape);
                Point after = afterMoveStates.get(shape);
                return before != null && (before.x != after.x || before.y != after.y);
            });

            if (moved) {
                Command moveCmd = new MoveCommand(
                    new ArrayList<>(selectedShapes),
                    new HashMap<>(beforeMoveStates),
                    afterMoveStates
                );
                commandManager.executeCommand(moveCmd);
            }
        }

        isDraggingShapes = false;
        prevMousePoint = null;
        beforeMoveStates.clear();
        repaint();
    }


    // 러버밴드 선택 완료 처리
    private void finalizeRubberBandSelection(MouseEvent e) {
        dragEnd = e.getPoint();
        Rectangle box = getRubberBandBounds();

        if (!e.isShiftDown()) {
            selectedShapes.clear();
            for (Shape shape : shapes) {
                shape.deselect();
            }
        }

        for (Shape shape : shapes) {
            Rectangle bounds = new Rectangle(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());
            if (box.intersects(bounds)) {
                shape.select();
                if (!selectedShapes.contains(shape)) {
                    selectedShapes.add(shape);
                }
            }
        }

        if (!selectedShapes.isEmpty()) {
            selectedShape = selectedShapes.get(0);
        }

        dragStart = dragEnd = null;
        isRubberBandActive = false;
        repaint();
    }

    private Rectangle getRubberBandBounds() {
        int x = Math.min(dragStart.x, dragEnd.x);
        int y = Math.min(dragStart.y, dragEnd.y);
        int width = Math.abs(dragStart.x - dragEnd.x);
        int height = Math.abs(dragStart.y - dragEnd.y);
        return new Rectangle(x, y, width, height);
    }

    public void addShape(Shape shape) {
        Command addCmd = new AddShapeCommand(shapes, shape);
        commandManager.executeCommand(addCmd);
        shape.addObserver(this);  // Add CanvasPanel as an observer
        repaint();
    }

    @Override
    public void onShapeChanged(Shape shape) {
        if (statePanel != null && shape != null) {
            statePanel.updateShapeInfo(shape);
        }
        repaint();
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public ToolManager getToolManager() {
        return toolManager;
    }

    public List<Shape> getSelectedShapes() {
        return selectedShapes;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        for (Shape shape : shapes) {
            shape.draw(g2d);
        }

        // 드래그 러버밴드 박스
        if (isRubberBandActive && dragStart != null && dragEnd != null) {
            Rectangle box = getRubberBandBounds();
            g2d.setColor(new Color(0, 120, 215, 64)); // 반투명 파랑
            g2d.fill(box);
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(1));
            g2d.draw(box);
        }

        Tool tool = toolManager.getCurrentTool();
        if (tool != null && tool.isActive()) {
            tool.draw(g2d);
        }
    }

    private void showInlineTextEditor(TextShape textShape) {
        removeInlineTextEditor();

        textEditor = new JTextField(textShape.getText());
        Font font = new Font(textShape.getFontName(), Font.PLAIN, textShape.getFontSize());
        textEditor.setFont(font);

        FontMetrics metrics = getFontMetrics(font);
        int textWidth = metrics.stringWidth(textShape.getText());
        int textHeight = metrics.getHeight();

        textEditor.setOpaque(false);
        textEditor.setBackground(new Color(0,0,0,0));
        textEditor.setBounds(textShape.getX() - 2, textShape.getY() - 2,
            textWidth + 4, textHeight + 4);

        add(textEditor);
        textEditor.requestFocus(); // 커서를 가져감

        // 다른 곳을 클릭했을 때 입력 저장
        textEditor.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String oldText = textShape.getText();
                String newText = textEditor.getText();

                Command changeText = new PropertyChangeCommand<>(
                    textShape::getText,
                    textShape::setText,
                    oldText,
                    newText
                );
                commandManager.executeCommand(changeText);
                textShape.setText(newText);
                removeInlineTextEditor();
                repaint();
            }
        });

        revalidate();
        repaint();
    }

    private void removeInlineTextEditor() {
        if (textEditor != null) {
            remove(textEditor);
            textEditor = null;
            revalidate();
            repaint();
        }
    }

    public void setStatePanel(StatePanel statePanel) {
        this.statePanel = statePanel;
    }

    public void setCurrentTool(String toolName) {
        toolManager.setCurrentTool(toolName);
        if (statePanel != null) {
            statePanel.updateToolState(toolName);
        }
    }

    public StatePanel getStatePanel() {
        return statePanel;
    }
}