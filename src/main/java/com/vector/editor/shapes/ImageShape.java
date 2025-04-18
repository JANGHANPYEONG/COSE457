package com.vector.editor.shapes;

import com.vector.editor.core.Shape;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
        Graphics2D g2 = (Graphics2D) g;

        if (image == null) return;

        int drawX = x;
        int drawY = y;
        int drawWidth = width;
        int drawHeight = height;

        // 비율 유지
        if (maintainAspectRatio) {
            int imgWidth = image.getWidth(null);
            int imgHeight = image.getHeight(null);
            if (imgWidth > 0 && imgHeight > 0) {
                float imgRatio = (float) imgWidth / imgHeight;
                float shapeRatio = (float) width / height;

                if (shapeRatio > imgRatio) {
                    drawWidth = (int) (height * imgRatio);
                    drawX = x + (width - drawWidth) / 2;
                } else {
                    drawHeight = (int) (width / imgRatio);
                    drawY = y + (height - drawHeight) / 2;
                }
            }
        }

        g2.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);

        if (isSelected()) {
            drawSelectionUI(g2);
        }
    }

    @Override
    public boolean contains(int px, int py) {
        int drawX = x;
        int drawY = y;
        int drawWidth = width;
        int drawHeight = height;

        if (maintainAspectRatio && image != null) {
            int imgWidth = image.getWidth(null);
            int imgHeight = image.getHeight(null);
            if (imgWidth > 0 && imgHeight > 0) {
                float imgRatio = (float) imgWidth / imgHeight;
                float shapeRatio = (float) width / height;

                if (shapeRatio > imgRatio) {
                    drawWidth = (int) (height * imgRatio);
                    drawX = x + (width - drawWidth) / 2;
                } else {
                    drawHeight = (int) (width / imgRatio);
                    drawY = y + (height - drawHeight) / 2;
                }
            }
        }

        if ((px >= drawX && px <= drawX + drawWidth) && (py >= drawY && py <= drawY + drawHeight)) {
            return true;
        }

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