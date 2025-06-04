package com.vector.editor.model.shape;

import java.awt.*;
import java.beans.PropertyChangeSupport;

public class TextShape extends Shape {
    private String text;
    private Font font;
    private Color textColor;
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    public TextShape(int x, int y, String text, Font font, Color textColor) {
        super(x, y, 0, 0, null, null, 0);
        this.text = text;
        this.font = font;
        this.textColor = textColor;
        updateBounds();
    }

    @Override
    public void draw(Graphics2D g) {
        g.setFont(font);
        g.setColor(textColor);
        g.drawString(text, x, y + g.getFontMetrics().getAscent());
        
        if (selected) {
            drawSelectionUI(g);
        }
    }

    @Override
    public boolean contains(int px, int py) {
        return px >= x && px <= x + width && py >= y - height && py <= y;
    }

    private void updateBounds() {
        if (text == null || text.isEmpty()) {
            width = 0;
            height = 0;
            return;
        }

        FontMetrics metrics = new Canvas().getFontMetrics(font);
        width = metrics.stringWidth(text);
        height = metrics.getHeight();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String oldText = this.text;
        this.text = text;
        support.firePropertyChange("text", oldText, text);
        updateBounds();
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        Font oldFont = this.font;
        this.font = font;
        support.firePropertyChange("font", oldFont, font);
        updateBounds();
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color color) {
        Color oldColor = this.textColor;
        this.textColor = color;
        support.firePropertyChange("textColor", oldColor, color);
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
        setPosition(newX, newY);
        setSize(newW, newH);
    }
} 