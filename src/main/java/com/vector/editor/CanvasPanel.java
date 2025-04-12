package com.vector.editor;

import com.vector.editor.core.Shape;
import com.vector.editor.shapes.TextShape;
import com.vector.editor.tools.Tool;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class CanvasPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.WHITE;

    private List<Shape> shapes = new ArrayList<>();
    private Shape selectedShape = null;
    private JTextField textEditor = null;

    private Tool currentTool;
    
    public CanvasPanel() {
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(800, 600));
        
        // Add mouse listeners for drawing
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentTool != null && currentTool.isActive()) {
                    currentTool.mousePressed(e);
                } else {
                    handleShapeSelection(e);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentTool != null && currentTool.isActive()) {
                    currentTool.mouseReleased(e);
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
                if (currentTool != null && currentTool.isActive()) {
                    currentTool.mouseDragged(e);
                }
            }
        });
    }

    // 선택 도구일 때 도형 선택 및 텍스트 편집 처리
    private void handleShapeSelection(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        selectedShape = null;

        for (Shape shape : shapes) {
            if (shape.contains(mouseX, mouseY)) {
                shape.select();
                selectedShape = shape;
            } else {
                shape.deselect();
            }
        }

        repaint();

        if (selectedShape instanceof TextShape textShape) {
            showInlineTextEditor(textShape);
        } else {
            removeInlineTextEditor();
        }
    }


    public void addShape(Shape shape) {
        shapes.add(shape);
        repaint();
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