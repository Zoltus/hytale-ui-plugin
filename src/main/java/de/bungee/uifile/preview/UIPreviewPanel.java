package de.bungee.uifile.preview;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class UIPreviewPanel extends JPanel {
    private final Project project;
    private final UIComponentRenderer renderer;
    private final JScrollPane scrollPane;
    private VirtualFile currentFile;
    private Document currentDocument;
    private DocumentListener documentListener;

    public UIPreviewPanel(Project project) {
        super(new BorderLayout());
        this.project = project;
        this.renderer = new UIComponentRenderer();

        // Renderer in ScrollPane einbetten
        scrollPane = new JScrollPane(renderer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // Toolbar mit Zoom-Buttons
        JPanel toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);

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

    public void updatePreview(VirtualFile file) {
        if (file == null) return;

        // Entferne alten DocumentListener
        if (currentDocument != null && documentListener != null) {
            currentDocument.removeDocumentListener(documentListener);
        }

        currentFile = file;

        try {
            String content = new String(file.contentsToByteArray(), file.getCharset());
            renderContent(content);

            // Füge neuen DocumentListener hinzu für Echtzeit-Updates
            Document document = FileDocumentManager.getInstance().getDocument(file);
            if (document != null) {
                currentDocument = document;
                documentListener = new DocumentListener() {
                    @Override
                    public void documentChanged(@NotNull DocumentEvent event) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            String updatedContent = document.getText();
                            renderContent(updatedContent);
                        });
                    }
                };
                currentDocument.addDocumentListener(documentListener);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void renderContent(String content) {
        UIModel model = UIModelParser.parse(content);
        renderer.setModel(model);
        renderer.repaint();
    }
}

