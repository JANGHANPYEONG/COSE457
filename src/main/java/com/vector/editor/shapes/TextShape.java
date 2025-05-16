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
    private int textWidth;
    private int textHeight;

    public TextShape(int x, int y, int width, int height,
                    Color fillColor, Color strokeColor, int strokeWidth,
                    String text, String fontName, int fontSize) {
        super(x, y, width, height, fillColor, strokeColor, strokeWidth);
        this.text = text;
        this.fontName = fontName;
        this.fontSize = fontSize;
        updateTextDimensions();
    }

    private void updateTextDimensions() {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font(fontName, Font.PLAIN, fontSize);
        FontMetrics metrics = g2d.getFontMetrics(font);
        textWidth = metrics.stringWidth(text);
        textHeight = metrics.getHeight();
        g2d.dispose();
        
        // Update shape dimensions to match text
        this.width = textWidth;
        this.height = textHeight;
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
        return (px >= x && px <= x + textWidth) && 
               (py >= y && py <= y + textHeight);
    }

    @Override
    public void resize(int dw, int dh) {
        // 텍스트 크기 조절 시 폰트 크기 조절
        if (dw != 0) {
            double scale = (double)(width + dw) / width;
            fontSize = (int)(fontSize * scale);
            updateTextDimensions();
        }
        notifyObservers();
    }

    @Override
    public Rectangle getBoundsWithStroke() {
        return new Rectangle(x, y, textWidth, textHeight);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        updateTextDimensions();
        notifyObservers();
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
        updateTextDimensions();
        notifyObservers();
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        updateTextDimensions();
        notifyObservers();
    }
} 