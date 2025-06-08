package com.vector.editor.model.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class FreeDrawPath extends Shape {
    private static final long serialVersionUID = 1L;
    
    private List<Point> points;

    public FreeDrawPath(Color strokeColor, int strokeWidth) {
        super(0, 0, 0, 0, null, strokeColor, strokeWidth);
        this.points = new ArrayList<>();
    }

    public void addPoint(Point point) {
        points.add(point);
        updateBounds();
    }

    private void updateBounds() {
        if (points.isEmpty()) return;

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Point p : points) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
        }

        x = minX;
        y = minY;
        width = maxX - minX;
        height = maxY - minY;
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (points.size() < 2) return;

        g2d.setColor(strokeColor);
        g2d.setStroke(new java.awt.BasicStroke(strokeWidth));

        Point prev = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            Point curr = points.get(i);
            g2d.drawLine(prev.x, prev.y, curr.x, curr.y);
            prev = curr;
        }
        
        if (selected) {
            drawSelectionUI(g2d);
        }
    }

    @Override
    public boolean contains(int px, int py) {
        if (points.size() < 2) return false;

        // 선과 점 사이의 거리를 계산하여 선택 영역을 판단
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            if (distanceToLine(px, py, p1.x, p1.y, p2.x, p2.y) <= strokeWidth) {
                return true;
            }
        }
        return false;
    }

    private double distanceToLine(int px, int py, int x1, int y1, int x2, int y2) {
        double A = px - x1;
        double B = py - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1;

        if (len_sq != 0) {
            param = dot / len_sq;
        }

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        double dx = px - xx;
        double dy = py - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public void move(int dx, int dy) {
        for (Point p : points) {
            p.x += dx;
            p.y += dy;
        }
        updateBounds();
    }

    @Override
    public Point[] getHandlePoints() {
        // 바운딩 박스 4모서리
        return new Point[] {
            new Point(x, y),
            new Point(x + width, y),
            new Point(x, y + height),
            new Point(x + width, y + height)
        };
    }

    @Override
    public int getHandleAt(int mx, int my) {
        Point[] handles = getHandlePoints();
        for (int i = 0; i < handles.length; i++) {
            Point p = handles[i];
            Rectangle r = new Rectangle(p.x - HANDLE_SIZE/2, p.y - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
            if (r.contains(mx, my)) return i;
        }
        return -1;
    }

    @Override
    public void resize(int handleIndex, int mx, int my) {
        // 바운딩 박스 기준 스케일링
        int oldX = x, oldY = y, oldW = width, oldH = height;
        int newX = x, newY = y, newW = width, newH = height;
        switch (handleIndex) {
            case 0: // 좌상단
                newW = width + (x - mx);
                newH = height + (y - my);
                newX = mx;
                newY = my;
                break;
            case 1: // 우상단
                newW = mx - x;
                newH = height + (y - my);
                newY = my;
                break;
            case 2: // 좌하단
                newW = width + (x - mx);
                newH = my - y;
                newX = mx;
                break;
            case 3: // 우하단
                newW = mx - x;
                newH = my - y;
                break;
        }
        if (newW < 5) newW = 5;
        if (newH < 5) newH = 5;
        // 모든 점을 비율에 맞게 이동
        double scaleX = oldW == 0 ? 1 : (double)newW / oldW;
        double scaleY = oldH == 0 ? 1 : (double)newH / oldH;
        for (Point p : points) {
            p.x = newX + (int)((p.x - oldX) * scaleX);
            p.y = newY + (int)((p.y - oldY) * scaleY);
        }
        updateBounds();
    }
} 