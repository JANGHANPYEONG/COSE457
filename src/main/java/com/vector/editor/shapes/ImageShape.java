package com.vector.editor.shapes;

import com.vector.editor.core.Shape;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

public class ImageShape extends Shape {
    private Image image;
    private boolean maintainAspectRatio;

    public ImageShape(int x, int y, int width, int height,
                     Color fillColor, Color strokeColor, int strokeWidth,
                     Image image, boolean maintainAspectRatio) {
        super(x, y, width, height, fillColor, strokeColor, strokeWidth);
        this.image = image;
        this.maintainAspectRatio = maintainAspectRatio;
    }

    @Override
    public void draw(Graphics g) {
        // TODO: Implement image drawing
    }

    @Override
    public boolean contains(int px, int py) {
        // TODO: Implement point containment check for image
        return false;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        notifyObservers();
    }

    public boolean isMaintainAspectRatio() {
        return maintainAspectRatio;
    }

    public void setMaintainAspectRatio(boolean maintainAspectRatio) {
        this.maintainAspectRatio = maintainAspectRatio;
        notifyObservers();
    }
} 