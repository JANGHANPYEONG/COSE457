package com.vector.editor.service;

import com.vector.editor.model.Document;
import java.io.*;

public class FileService {
    public void saveDocument(Document document, File file) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(file))) {
            out.writeObject(document);
            document.setModified(false);
        }
    }

    public Document loadDocument(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(file))) {
            Document doc = (Document) in.readObject();
            doc.setModified(false);
            return doc;
        }
    }
} 