package com.vector.editor.view;

import com.vector.editor.controller.StateManager;
import com.vector.editor.controller.ToolManager;
import com.vector.editor.model.Document;
import com.vector.editor.model.shape.Shape;
import com.vector.editor.service.FileService;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import com.vector.editor.command.CommandManager;

public class MainFrame extends JFrame {
    private Document document;
    private ToolManager toolManager;
    private CanvasView canvasView;
    private ToolPanel toolPanel;
    private ColorPanel colorPanel;
    private File currentFile;
    private FileService fileService;
    private CommandManager commandManager;
    private StateManager stateManager;
    private StatePanel statePanel;

    public MainFrame() {
        document = new Document();
        commandManager = new CommandManager();
        toolManager = new ToolManager(document, commandManager);
        fileService = new FileService();
        stateManager = new StateManager(document, commandManager);
        
        // ToolManager의 currentTool 변경 시 CanvasView에 반영
        toolManager.addPropertyChangeListener(evt -> {
            if ("currentTool".equals(evt.getPropertyName())) {
                if (canvasView != null) {
                    canvasView.setTool((com.vector.editor.controller.tool.Tool) evt.getNewValue());
                }
                // SelectTool에서 다른 Tool로 바뀔 때 선택 해제
                Object oldTool = evt.getOldValue();
                Object newTool = evt.getNewValue();
                if (oldTool != null && oldTool instanceof com.vector.editor.controller.tool.SelectTool
                    && !(newTool instanceof com.vector.editor.controller.tool.SelectTool)) {
                    document.clearSelection();
                }
            }
        });

        stateManager.addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals(StateManager.PROPERTY_POSITION_CHANGED)
                || evt.getPropertyName().equals(StateManager.PROPERTY_SIZE_CHANGED)) {
                canvasView.repaint();
            }
        });

        setTitle("Vector Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        setupUI();
        // KeyInputManager 등록
        KeyInputManager keyInputManager = new KeyInputManager(toolManager, commandManager, document, canvasView);
        this.addKeyListener(keyInputManager);
        this.setFocusable(true);
        this.requestFocusInWindow();
        keyInputManager.registerGlobalKeyBindings(this);
    }

    private void setupUI() {
        // 메인 패널 설정
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 왼쪽 패널 (도구 + 색상)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(130, 1000));
        leftPanel.setBackground(Color.BLACK);
        
        // 툴 패널 추가
        toolPanel = new ToolPanel(toolManager);
        toolPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(toolPanel);
        leftPanel.add(Box.createVerticalStrut(15));
        
        // 색상 패널 추가
        colorPanel = new ColorPanel(document);
        JPanel colorPanelContainer = new JPanel(new BorderLayout());
        colorPanelContainer.setBackground(Color.BLACK);
        colorPanelContainer.add(colorPanel, BorderLayout.WEST);
        leftPanel.add(colorPanelContainer);
        
        mainPanel.add(leftPanel, BorderLayout.WEST);

        // 오른쪽 패널 (상태)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(130, 1000));
        rightPanel.setBackground(Color.BLACK);

        // 상태 패널 추가
        statePanel = new StatePanel(stateManager);
        rightPanel.add(statePanel);

        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        // 캔버스 뷰 추가
        canvasView = new CanvasView(document);
        mainPanel.add(canvasView, BorderLayout.CENTER);
        
        // 메뉴바 설정
        setupMenuBar();
        
        add(mainPanel);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.BLACK);

        // 파일 메뉴
        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(new Font("SansSerif", Font.BOLD, 14));
        fileMenu.setForeground(Color.LIGHT_GRAY);
        fileMenu.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        fileMenu.getPopupMenu().setBorder(BorderFactory.createEmptyBorder());

        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.setFont(new Font("SansSerif", Font.BOLD, 14));
        newMenuItem.setBackground(new Color(50, 50, 50));
        newMenuItem.setForeground(Color.LIGHT_GRAY);
        newMenuItem.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setFont(new Font("SansSerif", Font.BOLD, 14));
        openMenuItem.setBackground(new Color(50, 50, 50));
        openMenuItem.setForeground(Color.LIGHT_GRAY);
        openMenuItem.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveMenuItem.setBackground(new Color(50, 50, 50));
        saveMenuItem.setForeground(Color.LIGHT_GRAY);
        saveMenuItem.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        saveAsMenuItem.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveAsMenuItem.setBackground(new Color(50, 50, 50));
        saveAsMenuItem.setForeground(Color.LIGHT_GRAY);
        saveAsMenuItem.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setFont(new Font("SansSerif", Font.BOLD, 14));
        exitMenuItem.setBackground(new Color(50, 50, 50));
        exitMenuItem.setForeground(Color.LIGHT_GRAY);
        exitMenuItem.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        newMenuItem.addActionListener(e -> {
            if (document.isModified()) {
                int result = JOptionPane.showConfirmDialog(this,
                    "Do you want to save changes?",
                    "Save Changes",
                    JOptionPane.YES_NO_CANCEL_OPTION);
                
                if (result == JOptionPane.YES_OPTION) {
                    saveDocument();
                } else if (result == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }

            document = new Document();
            toolManager = new ToolManager(document, commandManager);
            stateManager = new StateManager(document, commandManager);
            canvasView = new CanvasView(document);
            colorPanel = new ColorPanel(document);
            toolPanel = new ToolPanel(toolManager);
            statePanel = new StatePanel(stateManager);
            
            getContentPane().removeAll();
            setupUI();
            setupEventListeners();
            revalidate();
            repaint();
        });
        
        openMenuItem.addActionListener(e -> {
            if (document.isModified()) {
                int result = JOptionPane.showConfirmDialog(this,
                    "Do you want to save changes?",
                    "Save Changes",
                    JOptionPane.YES_NO_CANCEL_OPTION);
                
                if (result == JOptionPane.YES_OPTION) {
                    saveDocument();
                } else if (result == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }
            
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    currentFile = fileChooser.getSelectedFile();
                    loadFile(currentFile);
                    
                    setTitle("Vector Editor - " + currentFile.getName());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Error loading file: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        saveMenuItem.addActionListener(e -> saveDocument());
        
        saveAsMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
                saveDocument();
            }
        });
        
        exitMenuItem.addActionListener(e -> {
            if (document.isModified()) {
                int result = JOptionPane.showConfirmDialog(this,
                    "Do you want to save changes?",
                    "Save Changes",
                    JOptionPane.YES_NO_CANCEL_OPTION);
                
                if (result == JOptionPane.YES_OPTION) {
                    saveDocument();
                } else if (result == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }
            System.exit(0);
        });
        
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        
        menuBar.add(fileMenu);
        
        setJMenuBar(menuBar);
    }

    private void saveDocument() {
        if (currentFile == null) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
            } else {
                return;
            }
        }
        
        try {
            fileService.saveDocument(document, currentFile);
            setTitle("Vector Editor - " + currentFile.getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error saving file: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFile(File file) {
        try {
            Document newDocument = fileService.loadDocument(file);
            this.document = newDocument;

            this.toolManager = new ToolManager(document, commandManager);
            this.stateManager = new StateManager(document, commandManager);
            this.canvasView = new CanvasView(document);
            this.colorPanel = new ColorPanel(document);
            this.toolPanel = new ToolPanel(toolManager);
            this.statePanel = new StatePanel(stateManager);
            
            getContentPane().removeAll();
            setupUI();
            setupEventListeners();
            revalidate();
            repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage());
        }
    }

    private void setupEventListeners() {
        // ToolManager 리스너 재설정
        toolManager.addPropertyChangeListener(evt -> {
            if ("currentTool".equals(evt.getPropertyName())) {
                if (canvasView != null) {
                    canvasView.setTool((com.vector.editor.controller.tool.Tool) evt.getNewValue());
                }
                Object oldTool = evt.getOldValue();
                Object newTool = evt.getNewValue();
                if (oldTool != null && oldTool instanceof com.vector.editor.controller.tool.SelectTool
                    && !(newTool instanceof com.vector.editor.controller.tool.SelectTool)) {
                    document.clearSelection();
                }
            }
        });

        // StateManager 리스너 재설정
        stateManager.addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals(StateManager.PROPERTY_POSITION_CHANGED)
                || evt.getPropertyName().equals(StateManager.PROPERTY_SIZE_CHANGED)) {
                canvasView.repaint();
            }
        });

        // KeyInputManager 재등록
        KeyInputManager keyInputManager = new KeyInputManager(toolManager, commandManager, document, canvasView);
        this.addKeyListener(keyInputManager);
        keyInputManager.registerGlobalKeyBindings(this);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
} 