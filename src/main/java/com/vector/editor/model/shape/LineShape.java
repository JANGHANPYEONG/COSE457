package com.vector.editor.model.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public class LineShape extends Shape {
    private static final long serialVersionUID = 1L;
    
    private int endX;
    private int endY;

    public LineShape(int x, int y, int endX, int endY, Color strokeColor, int strokeWidth) {
        super(x, y, 0, 0, null, strokeColor, strokeWidth);
        this.endX = endX;
        this.endY = endY;
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(strokeColor);
        g2d.setStroke(new java.awt.BasicStroke(strokeWidth));
        g2d.drawLine(x, y, endX, endY);
        
        if (selected) {
            drawSelectionUI(g2d);
        }
    }

    @Override
    public Rectangle getBounds() {
        int minX = Math.min(x, endX);
        int minY = Math.min(y, endY);
        int maxX = Math.max(x, endX);
        int maxY = Math.max(y, endY);
        return new Rectangle(minX - HANDLE_SIZE, minY - HANDLE_SIZE, (maxX - minX) + 2*HANDLE_SIZE, (maxY - minY) + 2*HANDLE_SIZE);
    }

    @Override
    protected void drawSelectionUI(Graphics2D g) {
        // 핸들 2개만 그림
        Point[] handles = getHandlePoints();
        g.setColor(Color.WHITE);
        for (Point p : handles) {
            g.fillRect(p.x - HANDLE_SIZE/2, p.y - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        }
        g.setColor(Color.BLACK);
        for (Point p : handles) {
            g.drawRect(p.x - HANDLE_SIZE/2, p.y - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        }
    }

    @Override
    public boolean contains(int px, int py) {
        // 선분 근처 또는 핸들 근처면 true
        if (new java.awt.geom.Line2D.Float(x, y, endX, endY).ptSegDist(px, py) <= strokeWidth + 3) return true;
        for (Point p : getHandlePoints()) {
            Rectangle r = new Rectangle(p.x - HANDLE_SIZE/2, p.y - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
            if (r.contains(px, py)) return true;
        }
        return false;
    }

    public void setEndPoint(int x, int y) {
        this.endX = x;
        this.endY = y;
    }

    @Override
    public void move(int dx, int dy) {
        super.move(dx, dy);
        endX += dx;
        endY += dy;
    }

    @Override
    public Point[] getHandlePoints() {
        return new Point[] {
            new Point(x, y), // 시작점
            new Point(endX, endY) // 끝점
        };
    }

    @Override
    public void resize(int handleIndex, int mx, int my) {
        if (handleIndex == 0) {
            this.x = mx;
            this.y = my;
        } else if (handleIndex == 1) {
            this.endX = mx;
            this.endY = my;
        }
    }

    public int getEndX() { return endX; }
    public int getEndY() { return endY; }

    @Override
    public int getHandleAt(int mx, int my) {
        // Line 도형의 핸들 위치 계산
        Point[] handles = getHandlePoints();
        for (int i = 0; i < handles.length; i++) {
            Point p = handles[i];
            // 핸들의 영역을 약간 더 크게 설정
            Rectangle r = new Rectangle(p.x - HANDLE_SIZE, p.y - HANDLE_SIZE, HANDLE_SIZE * 2, HANDLE_SIZE * 2);
            if (r.contains(mx, my)) {
                return i;
            }
        }
        return -1;
    }
} 