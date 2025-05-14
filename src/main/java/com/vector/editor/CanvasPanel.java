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
        this.toolManager = new ToolManager(this, commandManager);

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
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Tool tool = toolManager.getCurrentTool();
                if (tool != null && tool.isActive()) {
                    tool.mouseReleased(e);
                    return;
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
            }
        });
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

    public void setShapes(List<Shape> shapes) {
        this.shapes = shapes;
        selectedShapes.clear();
        selectedShape = null;

        // 옵저버 다시 등록
        for (Shape shape : shapes) {
            shape.addObserver(this);
        }

        repaint();
    }

    public void clearAllShapes() {
        shapes.clear();
        selectedShape = null;
        selectedShapes.clear();
        removeInlineTextEditor();
    }

    public StatePanel getStatePanel() {
        return statePanel;
    }
}