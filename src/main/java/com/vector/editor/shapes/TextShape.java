package com.vector.editor.shapes;

import com.vector.editor.core.Shape;
import java.awt.Color;
import java.awt.Graphics;

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
        // TODO: Implement text drawing
    }

    @Override
    public boolean contains(int px, int py) {
        // TODO: Implement point containment check for text
        return false;
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