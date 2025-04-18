package com.vector.editor.shapes;

import com.vector.editor.CanvasPanel;
import com.vector.editor.core.Shape;
import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class TextShape extends Shape {
    private String text;
    private String fontName;
    private int fontSize;

    public TextShape(int x, int y, int width, int height,
                    Color fillColor, Color strokeColor, int strokeWidth,
                    String text, String fontName, int fontSize) {
        super(x, y, width, height, fillColor, strokeColor, strokeWidth);
        this.text = text;
        this.fontName = fontName;
        this.fontSize = fontSize;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        Font font = new Font(fontName, Font.PLAIN, fontSize);
        g2.setFont(font);
        g2.setColor(fillColor);

        g2.drawString(text, x, y + fontSize);

        if (isSelected()) {
            drawSelectionUI(g2);
        }
    }

    @Override
    public boolean contains(int px, int py) {
        Font font = new Font(fontName, Font.PLAIN, fontSize);
        FontMetrics metrics = new Canvas().getFontMetrics(font);
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();

        if ((px >= x && px <= x + textWidth) && (py >= y && py <= y + textHeight)) {
            return true;
        }

        return false;
    }

    @Override
    public Rectangle getBoundsWithStroke() {
        Font font = new Font(fontName, Font.PLAIN, fontSize);

        BufferedImage dummy = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = dummy.createGraphics();
        FontMetrics metrics = g2.getFontMetrics(font);

        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        g2.dispose();

        return new Rectangle(x, y, textWidth, textHeight);
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        notifyObservers();
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
        notifyObservers();
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        notifyObservers();
    }
} 