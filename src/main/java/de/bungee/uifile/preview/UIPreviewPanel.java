package de.bungee.uifile.preview;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
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

    private final UIComponentRenderer renderer;
    private Document currentDocument;
    private DocumentListener documentListener;

    public UIPreviewPanel(Project project) {
        super(new BorderLayout());
        this.renderer = new UIComponentRenderer();

        // Renderer in ScrollPane einbetten
        JBScrollPane scrollPane = new JBScrollPane(renderer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_UNIT_INCREMENT);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(SCROLL_UNIT_INCREMENT);

        add(scrollPane, BorderLayout.CENTER);

        // Toolbar mit Zoom-Buttons
        add(createToolbar(), BorderLayout.NORTH);

        setBorder(JBUI.Borders.empty());
        setBackground(JBColor.background());
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        toolbar.setBackground(JBColor.background());
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, JBColor.border()));

        JButton zoomInButton = new JButton("+");
        zoomInButton.setToolTipText("Zoom In");
        zoomInButton.addActionListener(e -> renderer.zoomIn());

        JButton zoomOutButton = new JButton("-");
        zoomOutButton.setToolTipText("Zoom Out");
        zoomOutButton.addActionListener(e -> renderer.zoomOut());

        JButton resetButton = new JButton("100%");
        resetButton.setToolTipText("Reset Zoom");
        resetButton.addActionListener(e -> renderer.resetZoom());

        toolbar.add(zoomInButton);
        toolbar.add(zoomOutButton);
        toolbar.add(resetButton);

        return toolbar;
    }

    public void updatePreview(@Nullable VirtualFile file) {
        if (file == null) {
            return;
        }

        // Entferne alten DocumentListener
        removeCurrentDocumentListener();

        try {
            String content = new String(file.contentsToByteArray(), file.getCharset());
            renderContent(content);

            // Füge neuen DocumentListener hinzu für Echtzeit-Updates
            attachDocumentListener(file);
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
                    ApplicationManager.getApplication().invokeLater(() ->
                        renderContent(document.getText())
                    );
                }
            };
            currentDocument.addDocumentListener(documentListener);
        }
    }

    private void renderContent(@NotNull String content) {
        UIModel model = UIModelParser.parse(content);
        renderer.setModel(model);
        renderer.repaint();
    }

    /**
     * Cleanup-Methode zum Entfernen von Listeners
     */
    public void dispose() {
        removeCurrentDocumentListener();
        currentDocument = null;
    }
}

