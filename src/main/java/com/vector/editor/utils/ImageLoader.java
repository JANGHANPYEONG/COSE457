package com.vector.editor.utils;

import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.JFileChooser;

public class ImageLoader {

    public static Image loadImageFromFile(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Image File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image files", "png", "jpg", "jpeg", "gif", "bmp"));

        int result = fileChooser.showOpenDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return Toolkit.getDefaultToolkit().getImage(selectedFile.getAbsolutePath());
        }

        return null; // 사용자가 취소한 경우
    }
}
