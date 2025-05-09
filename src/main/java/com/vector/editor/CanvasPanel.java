package com.vector.editor;

import com.vector.editor.command.AddShapeCommand;
import com.vector.editor.command.Command;
import com.vector.editor.command.CommandManager;
import com.vector.editor.command.MoveCommand;
import com.vector.editor.core.Shape;
import com.vector.editor.core.Shape.HandlePosition;
import com.vector.editor.shapes.GroupShape;
import com.vector.editor.shapes.TextShape;
import com.vector.editor.tools.Tool;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class CanvasPanel extends JPanel {
    private CommandManager commandManager;

    private static final Color BACKGROUND_COLOR = Color.WHITE;

    private List<Shape> shapes = new ArrayList<>();
    private Shape selectedShape = null;
    private List<Shape> selectedShapes = new ArrayList<>();
    private JTextField textEditor = null;

    private Point dragStart = null;
    private Point dragEnd = null;
    private boolean isRubberBandActive = false;

    private Tool currentTool;

    private boolean isDraggingShape = false;
    private Point dragOffset = null;
    private Shape resizingShape = null;
    private HandlePosition resizingHandle = null;
    private Point prevMousePoint = null;
    
    public CanvasPanel(CommandManager commandManager) {
        this.commandManager = commandManager;

        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(800, 600));
        
        // Add mouse listeners for drawing
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();
                resizingShape = null;
                resizingHandle = null;
                isDraggingShape = false;

                for (Shape shape : shapes) {
                    if (shape.isSelected()) {
                        for (Map.Entry<HandlePosition, Rectangle> entry : shape.getResizeHandles().entrySet()) {
                            if (entry.getValue().contains(p)) {
                                resizingShape = shape;
                                resizingHandle = entry.getKey();
                                prevMousePoint = p;
                                return;
                            }
                        }

                        // 리사이징 핸들이 아닌 도형 내부를 클릭했을 경우
                        if (shape.contains(p.x, p.y)) {
                            resizingShape = null;
                            resizingHandle = null;
                            isDraggingShape = true;
                            prevMousePoint = p;
                            return;
                        }
                    }
                }

                if (currentTool != null && currentTool.isActive()) {
                    currentTool.mousePressed(e);
                } else if (SwingUtilities.isLeftMouseButton(e) && !e.isShiftDown()) {
                    // 드래그로 다중 선택 시작
                    dragStart = e.getPoint();
                    dragEnd = e.getPoint();
                    isRubberBandActive = true;
                }else {
                    handleShapeSelection(e);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (resizingShape != null) {
                    resizingShape = null;
                    resizingHandle = null;
                    prevMousePoint = null;
                    repaint();
                    return;
                }

                if (currentTool != null && currentTool.isActive()) {
                    currentTool.mouseReleased(e);
                } else if (isRubberBandActive) {
                    // 드래그 종료
                    dragEnd = e.getPoint();
                    Rectangle box = getRubberBandBounds();

                    selectedShapes.clear();
                    for (Shape shape : shapes) {
                        Rectangle bounds = new Rectangle(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());
                        if (box.intersects(bounds)) {
                            shape.select();
                            selectedShapes.add(shape);
                        } else {
                            shape.deselect();
                        }
                    }

                    dragStart = dragEnd = null;
                    isRubberBandActive = false;
                    repaint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentTool != null && currentTool.isActive()) {
                    currentTool.mouseClicked(e);
                }
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (resizingShape != null && resizingHandle != null && prevMousePoint != null) {
                    Point curr = e.getPoint();
                    int dx = curr.x - prevMousePoint.x;
                    int dy = curr.y - prevMousePoint.y;

                    switch (resizingHandle) {
                        case BOTTOM_RIGHT -> resizingShape.resize(dx, dy);
                        case TOP_LEFT -> {
                            resizingShape.move(dx, dy);
                            resizingShape.resize(-dx, -dy);
                        }
                        case TOP_RIGHT -> {
                            resizingShape.move(0, dy);
                            resizingShape.resize(dx, -dy);
                        }
                        case BOTTOM_LEFT -> {
                            resizingShape.move(dx, 0);
                            resizingShape.resize(-dx, dy);
                        }
                    }

                    prevMousePoint = curr;
                    repaint();
                    return;
                }

                if (isDraggingShape && prevMousePoint != null && selectedShape != null) {
                    Point curr = e.getPoint();
                    int dx = curr.x - prevMousePoint.x;
                    int dy = curr.y - prevMousePoint.y;

                    Command moveCommand = new MoveCommand(selectedShape, dx, dy);
                    commandManager.executeCommand(moveCommand);

                    prevMousePoint = curr;
                    repaint();
                    return;
                }

                if (currentTool != null && currentTool.isActive()) {
                    currentTool.mouseDragged(e);
                } else if (isRubberBandActive) {
                    dragEnd = e.getPoint();
                    repaint();
                }
            }
        });
    }

    private Rectangle getRubberBandBounds() {
        int x = Math.min(dragStart.x, dragEnd.x);
        int y = Math.min(dragStart.y, dragEnd.y);
        int width = Math.abs(dragStart.x - dragEnd.x);
        int height = Math.abs(dragStart.y - dragEnd.y);
        return new Rectangle(x, y, width, height);
    }

    // 선택 도구일 때 도형 선택 및 텍스트 편집 처리
    private void handleShapeSelection(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        boolean isShift = e.isShiftDown();
        boolean foundShape = false;

        for (Shape shape : shapes) {
            if (shape.contains(mouseX, mouseY)) {
                foundShape = true;

                if (isShift) {
                    if (selectedShapes.contains(shape)) {
                        shape.deselect();
                        selectedShapes.remove(shape);
                    } else {
                        shape.select();
                        selectedShapes.add(shape);
                    }
                } else {
                    // Shift가 안 눌려있으면 도형을 하나만 선택
                    for (Shape s : shapes) s.deselect();
                    selectedShapes.clear();

                    shape.select();
                    selectedShapes.add(shape);
                    selectedShape = shape;
                }

                break; // 가장 위에 있는 도형 하나만 처리
            }
        }

        // 선택된 도형이 없고, Shift도 안 눌렸으면 전체 선택 해제
        if (!foundShape && !isShift) {
            for (Shape s : shapes) s.deselect();
            selectedShapes.clear();
            selectedShape = null;
            removeInlineTextEditor();
        }

        // 텍스트 편집기
        if (!isShift && selectedShape instanceof TextShape) {
            TextShape textShape = (TextShape) selectedShape;
            showInlineTextEditor(textShape);
        } else {
            removeInlineTextEditor();
        }

        repaint();
    }

    public void addShape(Shape shape) {
        Command addCmd = new AddShapeCommand(shapes, shape);
        commandManager.executeCommand(addCmd);
        repaint();
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public void setCurrentTool(Tool tool) {
        if (currentTool != null) currentTool.deactivate();
        currentTool = tool;
        if (currentTool != null) currentTool.activate();
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

        if (currentTool != null && currentTool.isActive()) {
            currentTool.draw(g2d);
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
                textShape.setText(textEditor.getText());
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
} 