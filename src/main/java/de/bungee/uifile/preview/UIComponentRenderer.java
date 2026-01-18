package de.bungee.uifile.preview;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

/**
 * Renderer für UI-Komponenten im Preview-Panel
 */
public class UIComponentRenderer extends JPanel {
    private UIModel model;
    private double scale = 1.0;
    private static final double ZOOM_FACTOR = 0.1;
    private static final double MIN_SCALE = 0.25;
    private static final double MAX_SCALE = 3.0;

    public UIComponentRenderer() {
        setBackground(new JBColor(new Color(40, 44, 52), new Color(40, 44, 52)));
        setBorder(JBUI.Borders.empty(20));
    }

    public void setModel(UIModel model) {
        this.model = model;
        updatePreferredSize();
        revalidate();
        repaint();
    }

    public void zoomIn() {
        if (scale < MAX_SCALE) {
            scale += ZOOM_FACTOR;
            updatePreferredSize();
            revalidate();
            repaint();
        }
    }

    public void zoomOut() {
        if (scale > MIN_SCALE) {
            scale -= ZOOM_FACTOR;
            updatePreferredSize();
            revalidate();
            repaint();
        }
    }

    public void resetZoom() {
        scale = 1.0;
        updatePreferredSize();
        revalidate();
        repaint();
    }

    private void updatePreferredSize() {
        if (model != null && !model.getComponents().isEmpty()) {
            int maxWidth = 0;
            int totalHeight = 0;

            for (UIModel.UIComponent component : model.getComponents()) {
                if (component instanceof UIModel.GroupComponent) {
                    UIModel.GroupComponent group = (UIModel.GroupComponent) component;
                    maxWidth = Math.max(maxWidth, (int)(group.getWidth() * scale));
                    totalHeight += (int)(group.getHeight() * scale) + 20;
                }
            }

            setPreferredSize(new Dimension(maxWidth + 80, totalHeight + 80));
        } else {
            setPreferredSize(new Dimension(400, 300));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (model == null || model.getComponents().isEmpty()) {
            drawEmptyState(g);
            return;
        }

        Graphics2D g2d = (Graphics2D) g.create();

        // Aktiviere Anti-Aliasing für professionelles Aussehen
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Zentriere das Preview
        int x = 40;
        int y = 40;

        // Zeichne Grid im Hintergrund (optional)
        drawGrid(g2d);

        // Rendere alle Komponenten
        for (UIModel.UIComponent component : model.getComponents()) {
            component.render(g2d, x, y, scale);
            y += (int)(200 * scale) + 20; // Abstand zwischen Groups
        }

        // Zoom-Level anzeigen
        drawZoomIndicator(g2d);

        g2d.dispose();
    }

    private void drawEmptyState(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String message = "Öffnen Sie eine .ui-Datei, um die Vorschau zu sehen";
        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        g2d.setFont(font);
        g2d.setColor(new Color(150, 150, 150));

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(message);
        int x = (getWidth() - textWidth) / 2;
        int y = getHeight() / 2;

        g2d.drawString(message, x, y);
        g2d.dispose();
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(new Color(50, 54, 62));
        g2d.setStroke(new BasicStroke(0.5f));

        int gridSize = (int)(20 * scale);

        // Vertikale Linien
        for (int x = 0; x < getWidth(); x += gridSize) {
            g2d.drawLine(x, 0, x, getHeight());
        }

        // Horizontale Linien
        for (int y = 0; y < getHeight(); y += gridSize) {
            g2d.drawLine(0, y, getWidth(), y);
        }
    }

    private void drawZoomIndicator(Graphics2D g2d) {
        String zoomText = String.format("%.0f%%", scale * 100);
        Font font = new Font("Segoe UI", Font.PLAIN, 11);
        g2d.setFont(font);
        g2d.setColor(new Color(150, 150, 150));

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(zoomText);

        g2d.drawString(zoomText, getWidth() - textWidth - 10, getHeight() - 10);
    }
}

