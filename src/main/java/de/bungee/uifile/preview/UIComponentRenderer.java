package de.bungee.uifile.preview;

import com.intellij.ui.JBColor;
import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UIComponentRenderer extends JPanel {
    private UIModel model;
    private double scale = 1.0;

    public void setModel(UIModel model) {
        this.model = model;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (model == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.scale(scale, scale);

        for (UIModel.GroupComponent group : model.getTopLevelGroups()) {
            layoutGroup(group, 0, 0, group.prefWidth, group.prefHeight);
            drawComponent(g2, group);
        }
    }

    private void layoutGroup(UIModel.GroupComponent parent, int x, int y, int width, int height) {
        parent.setBounds(x, y, width, height);
        int pad = parent.getPadding();
        int innerX = x + pad;
        int innerY = y + pad;
        int innerW = width - (2 * pad);
        int innerH = height - (2 * pad);

        String mode = parent.getLayoutMode();

        // 1. Berechne festen Platzbedarf und Flex-Summe
        float totalFlex = 0;
        int fixedSize = 0;
        for (UIModel.Component child : parent.getChildren()) {
            totalFlex += child.flexWeight;
            if (child.flexWeight == 0) {
                fixedSize += (mode.equals("Left") || mode.equals("Right")) ? child.prefWidth : child.prefHeight;
            }
        }

        // 2. Verteile restlichen Platz
        int availableSpace = (mode.equals("Left") || mode.equals("Right")) ? innerW : innerH;
        int remainingSpace = Math.max(0, availableSpace - fixedSize);

        int currentPos = (mode.equals("Left") || mode.equals("Top")) ? 0 : remainingSpace;

        for (UIModel.Component child : parent.getChildren()) {
            int childW = innerW;
            int childH = innerH;
            int size = (child.flexWeight > 0) ? (int) (remainingSpace * (child.flexWeight / totalFlex))
                : (mode.equals("Left") || mode.equals("Right") ? child.prefWidth : child.prefHeight);

            if (mode.equals("Left") || mode.equals("Right")) {
                child.setBounds(innerX + currentPos, innerY, size, innerH);
                currentPos += size;
            } else {
                child.setBounds(innerX, innerY + currentPos, innerW, size);
                currentPos += size;
            }

            if (child instanceof UIModel.GroupComponent) {
                layoutGroup((UIModel.GroupComponent) child, child.x, child.y, child.width, child.height);
            }
        }
    }

    private void drawComponent(Graphics2D g, UIModel.Component c) {
        if (c.background != null) {
            g.setColor(c.background);
            g.fillRect(c.x, c.y, c.width, c.height);
        }

        if (c instanceof UIModel.LabelComponent label) {
            drawLabel(g, label);
        } else if (c instanceof UIModel.ButtonComponent btn) {
            drawButton(g, btn);
        } else if (c instanceof UIModel.GroupComponent group) {
            for (UIModel.Component child : group.getChildren()) {
                drawComponent(g, child);
            }
        }
    }

    private void drawLabel(Graphics2D g, UIModel.LabelComponent l) {
        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.SIZE, (float) l.getFontSize());
        attributes.put(TextAttribute.WEIGHT, l.isBold() ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR);
        if (l.getLetterSpacing() > 0) {
            attributes.put(TextAttribute.TRACKING, l.getLetterSpacing() * 0.1f);
        }

        g.setFont(Font.getFont(attributes));
        g.setColor(l.getTextColor());

        FontMetrics fm = g.getFontMetrics();
        int textX = l.x;
        if ("Center".equalsIgnoreCase(l.getAlignment())) {
            textX = l.x + (l.width - fm.stringWidth(l.getText())) / 2;
        }
        g.drawString(l.getText(), textX, l.y + fm.getAscent() + (l.height - fm.getHeight()) / 2);
    }

    private void drawButton(Graphics2D g, UIModel.ButtonComponent b) {
        g.setColor(new JBColor(new Color(43, 53, 66), new Color(43, 53, 66)));
        g.fillRoundRect(b.x, b.y, b.width, b.height, 4, 4);
        g.setColor(Color.WHITE);
        Font f = g.getFont();
        g.setFont(f.deriveFont(Font.BOLD, 12f));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(b.getText(), b.x + (b.width - fm.stringWidth(b.getText())) / 2,
            b.y + (b.height + fm.getAscent()) / 2 - 2);
    }

    // Zoom-Methoden
    public void zoomIn() {
        scale *= 1.1;
        repaint();
    }

    public void zoomOut() {
        scale /= 1.1;
        repaint();
    }

    public void resetZoom() {
        scale = 1.0;
        repaint();
    }
}