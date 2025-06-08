package com.vector.editor.view;

import com.vector.editor.model.Document;
import com.vector.editor.model.shape.Shape;
import com.vector.editor.controller.tool.Tool;
import com.vector.editor.command.CommandManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class CanvasView extends JPanel implements PropertyChangeListener {
    private Document document;
    private transient Tool currentTool;
    private double zoom = 1.0;
    private ContextMenuManager contextMenuManager;
    private CommandManager commandManager;

    public CanvasView(Document document, CommandManager commandManager) {
        this.document = document;
        this.commandManager = commandManager;
        document.addPropertyChangeListener(this);
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // Initialize context menu manager
        this.contextMenuManager = new ContextMenuManager(document, this, commandManager);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentTool != null) {
                    currentTool.mousePressed(e, document);
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentTool != null) {
                    currentTool.mouseReleased(e, document);
                    repaint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) { // Right click
                    contextMenuManager.showContextMenu(CanvasView.this, e.getX(), e.getY());
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentTool != null) {
                    currentTool.mouseDragged(e, document);
                    repaint();
                }
            }
        });
    }

    public void setTool(Tool tool) {
        this.currentTool = tool;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // 안티앨리어싱 설정
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 줌 적용
        g2d.scale(zoom, zoom);
        
        // 모든 도형 그리기
        for (Shape shape : document.getShapes()) {
            shape.draw(g2d);
        }

        // selection box 그리기 (SelectTool 사용 시)
        if (currentTool != null && currentTool instanceof com.vector.editor.controller.tool.SelectTool) {
            com.vector.editor.controller.tool.SelectTool st = (com.vector.editor.controller.tool.SelectTool) currentTool;
            Rectangle selBox = st.getSelectionBox();
            if (selBox != null && selBox.width > 0 && selBox.height > 0) {
                Stroke oldStroke = g2d.getStroke();
                g2d.setColor(new Color(0, 120, 215, 80)); // 반투명 파랑
                g2d.fillRect(selBox.x, selBox.y, selBox.width, selBox.height);
                g2d.setColor(new Color(0, 120, 215));
                g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4,4}, 0));
                g2d.drawRect(selBox.x, selBox.y, selBox.width, selBox.height);
                g2d.setStroke(oldStroke);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        repaint();
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
        repaint();
    }

    public double getZoom() {
        return zoom;
    }
} 