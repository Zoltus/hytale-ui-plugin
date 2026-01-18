package de.bungee.uifile.preview;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class UIPreviewPanel extends JPanel implements Disposable {
    private static final Logger LOG = Logger.getInstance(UIPreviewPanel.class);
    private static final int SCROLL_UNIT_INCREMENT = 16;

    private static final JBColor PREVIEW_BG = new JBColor(Gray._24, Gray._24);

    private final UIComponentRenderer renderer;
    private Disposable currentListenerDisposable;
    private JLabel zoomLabel;

    public UIPreviewPanel() {
        super(new BorderLayout());
        this.renderer = new UIComponentRenderer();

        JBScrollPane scrollPane = new JBScrollPane(renderer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_UNIT_INCREMENT);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(SCROLL_UNIT_INCREMENT);

        scrollPane.getViewport().setBackground(PREVIEW_BG);

        // Add mouse wheel zoom (Ctrl + Mouse Wheel)
        renderer.addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                if (e.getWheelRotation() < 0) {
                    renderer.zoomIn();
                } else {
                    renderer.zoomOut();
                }
                updateZoomLabel();
                e.consume();
            }
        });

        add(scrollPane, BorderLayout.CENTER);
        add(createToolbar(), BorderLayout.NORTH);

        setBackground(PREVIEW_BG);

        // Add keyboard shortcuts
        setupKeyboardShortcuts();
    }

    private void setupKeyboardShortcuts() {
        InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap actionMap = getActionMap();

        // Zoom In: Ctrl + Plus / Ctrl + Equals (for US keyboards)
        inputMap.put(KeyStroke.getKeyStroke("control PLUS"), "zoomIn");
        inputMap.put(KeyStroke.getKeyStroke("control EQUALS"), "zoomIn");
        inputMap.put(KeyStroke.getKeyStroke("control ADD"), "zoomIn");
        actionMap.put("zoomIn", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                renderer.zoomIn();
                updateZoomLabel();
            }
        });

        // Zoom Out: Ctrl + Minus
        inputMap.put(KeyStroke.getKeyStroke("control MINUS"), "zoomOut");
        inputMap.put(KeyStroke.getKeyStroke("control SUBTRACT"), "zoomOut");
        actionMap.put("zoomOut", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                renderer.zoomOut();
                updateZoomLabel();
            }
        });

        // Reset Zoom: Ctrl + 0
        inputMap.put(KeyStroke.getKeyStroke("control 0"), "resetZoom");
        inputMap.put(KeyStroke.getKeyStroke("control NUMPAD0"), "resetZoom");
        actionMap.put("resetZoom", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                renderer.resetZoom();
                updateZoomLabel();
            }
        });
    }

    private JComponent createToolbar() {
        JPanel toolbarPanel = new JPanel(new BorderLayout());
        toolbarPanel.setBorder(JBUI.Borders.customLine(JBColor.border(), 0, 0, 1, 0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        leftPanel.setBackground(JBColor.background());

        // Create bigger, more clickable buttons
        JButton zoomInBtn = createStyledButton("+", "Zoom in (Ctrl + Plus)", e -> {
            renderer.zoomIn();
            updateZoomLabel();
        });

        JButton zoomOutBtn = createStyledButton("−", "Zoom out (Ctrl + Minus)", e -> {
            renderer.zoomOut();
            updateZoomLabel();
        });

        JButton resetBtn = createStyledButton("⟲", "Reset zoom to 100% (Ctrl + 0)", e -> {
            renderer.resetZoom();
            updateZoomLabel();
        });

        // Create zoom label with better styling
        zoomLabel = new JLabel("100%");
        zoomLabel.setFont(zoomLabel.getFont().deriveFont(Font.BOLD, 13f));
        zoomLabel.setBorder(JBUI.Borders.empty(0, 12, 0, 8));
        zoomLabel.setForeground(JBColor.foreground());
        zoomLabel.setPreferredSize(JBUI.size(55, 40));
        zoomLabel.setHorizontalAlignment(SwingConstants.CENTER);
        zoomLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Add components with spacing
        leftPanel.add(zoomInBtn);
        leftPanel.add(zoomOutBtn);
        leftPanel.add(resetBtn);

        // Add separator with proper sizing
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(JBUI.size(1, 30));
        separator.setBackground(JBColor.border());
        leftPanel.add(Box.createHorizontalStrut(5));
        leftPanel.add(separator);

        leftPanel.add(zoomLabel);

        toolbarPanel.add(leftPanel, BorderLayout.WEST);

        return toolbarPanel;
    }

    private JButton createStyledButton(String text, String tooltip, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setFocusable(false);
        button.addActionListener(listener);

        // Make buttons larger
        button.setPreferredSize(JBUI.size(40, 40));
        button.setMinimumSize(JBUI.size(40, 40));
        button.setMaximumSize(JBUI.size(40, 40));

        // Larger font for symbols - centered
        button.setFont(button.getFont().deriveFont(Font.BOLD, 20f));
        button.setMargin(JBUI.emptyInsets());
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);

        // Make button content centered and uniform
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(JBUI.CurrentTheme.ActionButton.hoverBackground());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(null);
            }
        });

        return button;
    }

    private void updateZoomLabel() {
        int zoomPercent = renderer.getZoomPercent();
        zoomLabel.setText(zoomPercent + "%");
    }

    public void updatePreview(@Nullable VirtualFile file) {
        if (file == null) {
            return;
        }

        removeCurrentDocumentListener();

        try {
            Document document = FileDocumentManager.getInstance().getDocument(file);
            String content;
            if (document != null) {
                content = document.getText();
                attachDocumentListener(file);
            } else {
                content = new String(file.contentsToByteArray(), file.getCharset());
            }

            renderContent(content);
        } catch (IOException e) {
            LOG.error("Failed to load preview for file: " + file.getPath(), e);
        }
    }

    private void removeCurrentDocumentListener() {
        if (currentListenerDisposable != null) {
            Disposer.dispose(currentListenerDisposable);
            currentListenerDisposable = null;
        }
    }

    private void attachDocumentListener(@NotNull VirtualFile file) {
        Document document = FileDocumentManager.getInstance().getDocument(file);
        if (document != null) {
            currentListenerDisposable = Disposer.newDisposable();

            DocumentListener documentListener = new DocumentListener() {
                @Override
                public void documentChanged(@NotNull DocumentEvent event) {
                    ApplicationManager.getApplication().invokeLater(() ->
                        renderContent(document.getText())
                    );
                }
            };

            document.addDocumentListener(documentListener, currentListenerDisposable);
        }
    }

    private void renderContent(@NotNull String content) {
        UIModel model = UIModelParser.parse(content);
        renderer.setModel(model);

        renderer.revalidate();
        renderer.repaint();
    }

    @Override
    public void dispose() {
        removeCurrentDocumentListener();
    }
}