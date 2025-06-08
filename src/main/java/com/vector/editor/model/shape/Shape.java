package com.vector.editor.model.shape;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public abstract class Shape implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected int x, y;
    protected int width, height;
    protected Color fillColor;
    protected Color strokeColor;
    protected int strokeWidth;
    protected boolean selected;
    protected int zOrder;
    private PropertyChangeSupport support;

    // 선택 UI 관련 상수
    protected static final int SELECTION_PADDING = 2;
    protected static final int HANDLE_SIZE = 6;
    protected static final float[] DASH_PATTERN = {5.0f};

    // 속성 이름 상수
    public static final String PROPERTY_POSITION = "position";
    public static final String PROPERTY_SIZE = "size";
    public static final String PROPERTY_FILL_COLOR = "fillColor";
    public static final String PROPERTY_STROKE_COLOR = "strokeColor";
    public static final String PROPERTY_STROKE_WIDTH = "strokeWidth";
    public static final String PROPERTY_SELECTED = "selected";
    public static final String PROPERTY_ZORDER = "zOrder";

    public Shape(int x, int y, int width, int height, Color fillColor, Color strokeColor, int strokeWidth) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.selected = false;
        this.zOrder = 0;
        this.support = new PropertyChangeSupport(this);
    }

    // 추상 메서드
    public abstract void draw(Graphics2D g);
    public abstract boolean contains(int x, int y);

    // PropertyChangeListener 관리
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    // 위치와 크기 설정
    public void setPosition(int x, int y) {
        Point oldPosition = new Point(this.x, this.y);
        this.x = x;
        this.y = y;
        support.firePropertyChange(PROPERTY_POSITION, oldPosition, new Point(x, y));
    }

    public void setX(int x) {
        int oldX = this.x;
        this.x = x;
        support.firePropertyChange(PROPERTY_POSITION, new Point(oldX, y), new Point(x, y));
    }

    public void setY(int y) {
        int oldY = this.y;
        this.y = y;
        support.firePropertyChange(PROPERTY_POSITION, new Point(x, oldY), new Point(x, y));
    }

    public void setSize(int width, int height) {
        Dimension oldSize = new Dimension(this.width, this.height);
        this.width = width;
        this.height = height;
        support.firePropertyChange(PROPERTY_SIZE, oldSize, new Dimension(width, height));
    }

    // 색상 설정
    public void setFillColor(Color color) {
        Color oldColor = this.fillColor;
        this.fillColor = color;
        support.firePropertyChange(PROPERTY_FILL_COLOR, oldColor, color);
    }

    public void setStrokeColor(Color color) {
        Color oldColor = this.strokeColor;
        this.strokeColor = color;
        support.firePropertyChange(PROPERTY_STROKE_COLOR, oldColor, color);
    }

    public void setStrokeWidth(int width) {
        int oldWidth = this.strokeWidth;
        this.strokeWidth = width;
        support.firePropertyChange(PROPERTY_STROKE_WIDTH, oldWidth, width);
    }

    // 선택 상태 설정
    public void setSelected(boolean selected) {
        boolean oldSelected = this.selected;
        this.selected = selected;
        support.firePropertyChange(PROPERTY_SELECTED, oldSelected, selected);
    }

    // Z-order 관련 메서드 추가
    public void setZOrder(int zOrder) {
        int oldZOrder = this.zOrder;
        this.zOrder = zOrder;
        support.firePropertyChange(PROPERTY_ZORDER, oldZOrder, zOrder);
    }

    public int getZOrder() {
        return zOrder;
    }

    // Getter 메서드들
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Color getFillColor() { return fillColor; }
    public Color getStrokeColor() { return strokeColor; }
    public int getStrokeWidth() { return strokeWidth; }
    public boolean isSelected() { return selected; }

    // 경계 상자 반환
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // 스트로크를 포함한 경계 상자 반환
    public Rectangle getBoundsWithStroke() {
        int inset = strokeWidth / 2;
        return new Rectangle(
            x - inset,
            y - inset,
            width + strokeWidth,
            height + strokeWidth
        );
    }

    // 선택 UI 그리기
    protected void drawSelectionUI(Graphics2D g) {
        // 선택 표시를 위한 점선 테두리
        Stroke originalStroke = g.getStroke();
        g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, DASH_PATTERN, 0));
        g.setColor(Color.BLACK);
        g.drawRect(x - SELECTION_PADDING, y - SELECTION_PADDING, 
                  width + SELECTION_PADDING * 2, height + SELECTION_PADDING * 2);
        g.setStroke(originalStroke);

        // 모서리 핸들 그리기
        drawCornerHandles(g);
    }

    // 모서리 핸들 그리기
    protected void drawCornerHandles(Graphics2D g) {
        // 흰색 배경
        g.setColor(Color.WHITE);
        g.fillRect(x - HANDLE_SIZE/2, y - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        g.fillRect(x + width - HANDLE_SIZE/2, y - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        g.fillRect(x - HANDLE_SIZE/2, y + height - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        g.fillRect(x + width - HANDLE_SIZE/2, y + height - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        
        // 검은색 테두리
        g.setColor(Color.BLACK);
        g.drawRect(x - HANDLE_SIZE/2, y - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        g.drawRect(x + width - HANDLE_SIZE/2, y - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        g.drawRect(x - HANDLE_SIZE/2, y + height - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        g.drawRect(x + width - HANDLE_SIZE/2, y + height - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
    }

    public void move(int dx, int dy) {
        setPosition(x + dx, y + dy);
    }

    // 4개 모서리 핸들 위치 반환
    public Point[] getHandlePoints() {
        return new Point[] {
            new Point(x, y), // 좌상단
            new Point(x + width, y), // 우상단
            new Point(x, y + height), // 좌하단
            new Point(x + width, y + height) // 우하단
        };
    }

    // 마우스 좌표가 어느 핸들 위에 있는지 반환 (없으면 -1)
    public int getHandleAt(int mx, int my) {
        Point[] handles = getHandlePoints();
        for (int i = 0; i < handles.length; i++) {
            Point p = handles[i];
            Rectangle r = new Rectangle(p.x - HANDLE_SIZE/2, p.y - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
            if (r.contains(mx, my)) return i;
        }
        return -1;
    }

    // 핸들 인덱스와 마우스 위치로 리사이즈
    public void resize(int handleIndex, int mx, int my) {
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
        // 최소 크기 제한
        if (newW < 5) newW = 5;
        if (newH < 5) newH = 5;
        setPosition(newX, newY);
        setSize(newW, newH);
    }
} 