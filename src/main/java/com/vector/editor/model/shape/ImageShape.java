package com.vector.editor.model.shape;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.beans.PropertyChangeSupport;

public class ImageShape extends Shape {
    private BufferedImage image;
    private String imagePath;
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    public ImageShape(int x, int y, File imageFile) throws IOException {
        super(x, y, 0, 0, null, null, 0);
        this.imagePath = imageFile.getAbsolutePath();
        this.image = ImageIO.read(imageFile);
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (image != null) {
            g2d.drawImage(image, x, y, width, height, null);
        }
        
        if (selected) {
            drawSelectionUI(g2d);
        }
    }

    @Override
    public boolean contains(int px, int py) {
        return new Rectangle(x, y, width, height).contains(px, py);
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        loadImage();
        support.firePropertyChange("imagePath", this.imagePath, imagePath);
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        BufferedImage oldImage = this.image;
        this.image = image;
        updateBounds();
        support.firePropertyChange("image", oldImage, image);
    }

    private void loadImage() {
        try {
            image = ImageIO.read(new File(imagePath));
            // 이미지 크기가 지정되지 않은 경우 원본 크기 사용
            if (width == 0 || height == 0) {
                width = image.getWidth();
                height = image.getHeight();
            }
        } catch (IOException e) {
            e.printStackTrace();
            // 이미지 로드 실패 시 빈 이미지 생성
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
    }

    private void updateBounds() {
        if (image != null) {
            width = image.getWidth();
            height = image.getHeight();
        }
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        // 이미지 크기 조정 시 품질 유지를 위한 리샘플링
        if (image != null) {
            BufferedImage resized = new BufferedImage(width, height, image.getType());
            Graphics2D g = resized.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(image, 0, 0, width, height, null);
            g.dispose();
            image = resized;
        }
    }
} 