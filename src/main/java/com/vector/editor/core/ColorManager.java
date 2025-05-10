package com.vector.editor.core;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ColorManager {
    private static ColorManager instance;
    private Color currentColor;
    private List<ColorChangeListener> listeners;

    private ColorManager() {
        this.currentColor = Color.BLACK;
        this.listeners = new ArrayList<>();
    }

    public static ColorManager getInstance() {
        if (instance == null) {
            instance = new ColorManager();
        }
        return instance;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
        notifyListeners();
    }

    public void addColorChangeListener(ColorChangeListener listener) {
        listeners.add(listener);
    }

    public void removeColorChangeListener(ColorChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (ColorChangeListener listener : listeners) {
            listener.onColorChanged(currentColor);
        }
    }

    public interface ColorChangeListener {
        void onColorChanged(Color newColor);
    }
} 