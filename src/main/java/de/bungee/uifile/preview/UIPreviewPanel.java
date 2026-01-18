package de.bungee.uifile.preview;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
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

public class UIPreviewPanel extends JPanel {
    private static final Logger LOG = Logger.getInstance(UIPreviewPanel.class);
    private static final int SCROLL_UNIT_INCREMENT = 16;

    // Hintergrundfarbe für den Preview-Bereich (neutral dunkel)
    private static final JBColor PREVIEW_BG = new JBColor(Gray._24, Gray._24);

    private final UIComponentRenderer renderer;
    private Document currentDocument;
    private DocumentListener documentListener;

    public UIPreviewPanel() {
        super(new BorderLayout());
        this.renderer = new UIComponentRenderer();

        // ScrollPane Setup
        JBScrollPane scrollPane = new JBScrollPane(renderer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_UNIT_INCREMENT);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(SCROLL_UNIT_INCREMENT);

        // Hintergrundfarbe des Viewports setzen, damit es beim Scrollen nicht flackert
        scrollPane.getViewport().setBackground(PREVIEW_BG);

        add(scrollPane, BorderLayout.CENTER);
        add(createToolbar(), BorderLayout.NORTH);

        setBackground(PREVIEW_BG);
    }

    private JPanel createToolbar() {
        // IntelliJ-Style Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        toolbar.setBackground(JBColor.background());
        toolbar.setBorder(JBUI.Borders.customLine(JBColor.border(), 0, 0, 1, 0));

        JButton zoomInButton = createToolbarButton("+", "Zoom In", e -> renderer.zoomIn());
        JButton zoomOutButton = createToolbarButton("-", "Zoom Out", e -> renderer.zoomOut());
        JButton resetButton = createToolbarButton("100%", "Reset Zoom", e -> renderer.resetZoom());

        toolbar.add(zoomInButton);
        toolbar.add(zoomOutButton);
        toolbar.add(resetButton);

        return toolbar;
    }

    private JButton createToolbarButton(String text, String tooltip, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setFocusable(false);
        button.addActionListener(listener);
        // Etwas flacheres Design für die IDE Integration
        button.putClientProperty("JButton.buttonType", "square");
        return button;
    }

    public void updatePreview(@Nullable VirtualFile file) {
        if (file == null) return;

        removeCurrentDocumentListener();

        try {
            // Immer den aktuellsten Content aus dem Dokument holen, falls vorhanden
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
        if (currentDocument != null && documentListener != null) {
            currentDocument.removeDocumentListener(documentListener);
            documentListener = null;
        }
    }

    private void attachDocumentListener(@NotNull VirtualFile file) {
        Document document = FileDocumentManager.getInstance().getDocument(file);
        if (document != null) {
            currentDocument = document;
            documentListener = new DocumentListener() {
                @Override
                public void documentChanged(@NotNull DocumentEvent event) {
                    // Benutze den Parser im Hintergrund-Thread, falls UI komplex wird
                    // Hier direkt für sofortiges Feedback:
                    ApplicationManager.getApplication().invokeLater(() ->
                        renderContent(document.getText())
                    );
                }
            };
            currentDocument.addDocumentListener(documentListener);
        }
    }

    private void renderContent(@NotNull String content) {
        // Hier wird die neue Parser-Logik aufgerufen
        UIModel model = UIModelParser.parse(content);
        renderer.setModel(model);

        // Wichtig für ScrollPane: Größe muss neu berechnet werden, wenn sich Content ändert
        renderer.revalidate();
        renderer.repaint();
    }

    public void dispose() {
        removeCurrentDocumentListener();
        currentDocument = null;
    }
}