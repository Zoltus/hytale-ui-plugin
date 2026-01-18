package de.bungee.uifile.preview;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repräsentiert das geparste UI-Modell
 */
public class UIModel {
    private final List<UIComponent> components;

    public UIModel() {
        this.components = new ArrayList<>();
    }

    public void addComponent(UIComponent component) {
        components.add(component);
    }

    public List<UIComponent> getComponents() {
        return components;
    }

    /**
     * Basis-Klasse für UI-Komponenten
     */
    public static abstract class UIComponent {
        protected String type;
        protected Map<String, String> properties;
        protected List<UIComponent> children;

        public UIComponent(String type) {
            this.type = type;
            this.properties = new HashMap<>();
            this.children = new ArrayList<>();
        }

        public String getType() {
            return type;
        }

        public void setProperty(String key, String value) {
            properties.put(key, value);
        }

        public String getProperty(String key) {
            return properties.get(key);
        }

        public Map<String, String> getProperties() {
            return properties;
        }

        public void addChild(UIComponent child) {
            children.add(child);
        }

        public List<UIComponent> getChildren() {
            return children;
        }

        public abstract void render(Graphics2D g2d, int x, int y, double scale);
    }

    /**
     * Group-Komponente
     */
    public static class GroupComponent extends UIComponent {
        private int width = 350;
        private int height = 180;
        private Color background = new Color(26, 26, 46);
        private int padding = 15;
        private boolean isSpacer = false;

        public GroupComponent() {
            super("Group");
        }

        public void setDimensions(int width, int height) {
            this.width = width;
            this.height = height;
            // Wenn Breite 0 ist, ist es ein Spacer
            if (width == 0) {
                isSpacer = true;
            }
        }

        public void setBackground(Color background) {
            this.background = background;
        }

        public void setPadding(int padding) {
            this.padding = padding;
        }

        @Override
        public void render(Graphics2D g2d, int x, int y, double scale) {
            int scaledWidth = (int)(width * scale);
            int scaledHeight = (int)(height * scale);
            int scaledPadding = (int)(padding * scale);

            // Spacer werden nicht gezeichnet, nehmen aber Platz ein
            if (isSpacer) {
                return;
            }

            // Hintergrund zeichnen
            g2d.setColor(background);
            g2d.fillRoundRect(x, y, scaledWidth, scaledHeight, 10, 10);

            // Border zeichnen
            g2d.setColor(new Color(60, 60, 80));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(x, y, scaledWidth, scaledHeight, 10, 10);

            // Berechne verfügbare Breite für Kinder
            int availableWidth = width - 2 * padding;

            // Kinder rendern mit korrekten Höhen
            int childY = y + scaledPadding;
            for (UIComponent child : children) {
                // Setze Breite für Komponenten die sie brauchen
                if (child instanceof LabelComponent) {
                    ((LabelComponent) child).setWidth(availableWidth);
                } else if (child instanceof ButtonComponent) {
                    ((ButtonComponent) child).setWidth(availableWidth);
                } else if (child instanceof TextFieldComponent) {
                    ((TextFieldComponent) child).setWidth(availableWidth);
                }

                child.render(g2d, x + scaledPadding, childY, scale);

                // Berechne die tatsächliche Höhe der Child-Komponente
                int childHeight = getComponentHeight(child, scale);
                childY += childHeight;
            }
        }

        private int getComponentHeight(UIComponent component, double scale) {
            if (component instanceof LabelComponent) {
                return (int)(((LabelComponent) component).getHeight() * scale);
            } else if (component instanceof ButtonComponent) {
                return (int)(((ButtonComponent) component).getHeight() * scale);
            } else if (component instanceof TextFieldComponent) {
                return (int)(((TextFieldComponent) component).getHeight() * scale);
            } else if (component instanceof GroupComponent) {
                // Spacer Groups (Groups ohne Eigenschaften außer Anchor)
                return (int)(((GroupComponent) component).getHeight() * scale);
            }
            return (int)(30 * scale); // Default
        }

        public int getWidth() { return width; }
        public int getHeight() { return height; }
    }

    /**
     * Label-Komponente
     */
    public static class LabelComponent extends UIComponent {
        private String text = "";
        private int fontSize = 12;
        private Color textColor = Color.WHITE;
        private boolean bold = false;
        private String alignment = "Left";
        private int height = 30;
        private int width = 300;

        public LabelComponent() {
            super("Label");
        }

        public void setText(String text) {
            this.text = text;
        }

        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
        }

        public void setTextColor(Color textColor) {
            this.textColor = textColor;
        }

        public void setBold(boolean bold) {
            this.bold = bold;
        }

        public void setAlignment(String alignment) {
            this.alignment = alignment;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        @Override
        public void render(Graphics2D g2d, int x, int y, double scale) {
            int scaledFontSize = (int)(fontSize * scale);
            int scaledHeight = (int)(height * scale);
            int scaledWidth = (int)(width * scale);

            Font font = new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, scaledFontSize);
            g2d.setFont(font);
            g2d.setColor(textColor);

            // Anti-Aliasing für bessere Textdarstellung
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);

            // Berechne X-Position basierend auf Alignment
            int textX = x;
            if (alignment.equalsIgnoreCase("Center")) {
                textX = x + (scaledWidth - textWidth) / 2;
            } else if (alignment.equalsIgnoreCase("Right")) {
                textX = x + scaledWidth - textWidth;
            }

            // Vertikale Zentrierung innerhalb der Höhe
            int textY = y + ((scaledHeight - fm.getHeight()) / 2) + fm.getAscent();

            g2d.drawString(text, textX, textY);
        }
    }

    /**
     * Button-Komponente
     */
    public static class ButtonComponent extends UIComponent {
        private String text = "";
        private Color background = new Color(0, 102, 204);
        private Color textColor = Color.WHITE;
        private int width = 150;
        private int height = 35;

        public ButtonComponent() {
            super("Button");
        }

        public void setText(String text) {
            this.text = text;
        }

        public void setBackground(Color background) {
            this.background = background;
        }

        public void setTextColor(Color textColor) {
            this.textColor = textColor;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        @Override
        public void render(Graphics2D g2d, int x, int y, double scale) {
            int scaledWidth = (int)(width * scale);
            int scaledHeight = (int)(height * scale);

            // Button Hintergrund
            g2d.setColor(background);
            g2d.fillRoundRect(x, y, scaledWidth, scaledHeight, 8, 8);

            // Button Border (hellerer)
            g2d.setColor(background.brighter());
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(x, y, scaledWidth, scaledHeight, 8, 8);

            // Text zentriert
            Font font = new Font("Segoe UI", Font.BOLD, (int)(14 * scale));
            g2d.setFont(font);
            g2d.setColor(textColor);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textX = x + (scaledWidth - textWidth) / 2;
            int textY = y + ((scaledHeight - fm.getHeight()) / 2) + fm.getAscent();

            g2d.drawString(text, textX, textY);
        }
    }

    /**
     * TextField/Input-Komponente
     */
    public static class TextFieldComponent extends UIComponent {
        private String placeholderText = "";
        private Color background = new Color(45, 45, 68);
        private Color textColor = Color.WHITE;
        private int width = 200;
        private int height = 30;

        public TextFieldComponent() {
            super("TextField");
        }

        public void setPlaceholderText(String placeholderText) {
            this.placeholderText = placeholderText;
        }

        public void setBackground(Color background) {
            this.background = background;
        }

        public void setTextColor(Color textColor) {
            this.textColor = textColor;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        @Override
        public void render(Graphics2D g2d, int x, int y, double scale) {
            int scaledWidth = (int)(width * scale);
            int scaledHeight = (int)(height * scale);

            // TextField Hintergrund
            g2d.setColor(background);
            g2d.fillRoundRect(x, y, scaledWidth, scaledHeight, 6, 6);

            // Border
            g2d.setColor(new Color(70, 70, 90));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(x, y, scaledWidth, scaledHeight, 6, 6);

            // Placeholder Text
            Font font = new Font("Segoe UI", Font.ITALIC, (int)(11 * scale));
            g2d.setFont(font);
            g2d.setColor(new Color(150, 150, 150));
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            FontMetrics fm = g2d.getFontMetrics();
            int textY = y + ((scaledHeight - fm.getHeight()) / 2) + fm.getAscent();

            g2d.drawString(placeholderText, x + (int)(8 * scale), textY);
        }
    }
}

