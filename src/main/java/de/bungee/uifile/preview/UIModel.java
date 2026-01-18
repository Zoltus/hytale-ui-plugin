package de.bungee.uifile.preview;

import com.intellij.ui.JBColor;
import com.intellij.ui.Gray;

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
        private static final int DEFAULT_WIDTH = 350;
        private static final int DEFAULT_HEIGHT = 180;
        private static final int DEFAULT_PADDING = 15;
        private static final int DEFAULT_COMPONENT_HEIGHT = 30;
        private static final int BORDER_RADIUS = 10;

        private static final JBColor DEFAULT_BACKGROUND = new JBColor(new Color(26, 26, 46), new Color(26, 26, 46));
        private static final JBColor BORDER_COLOR = new JBColor(new Color(60, 60, 80), new Color(60, 60, 80));

        private int width = DEFAULT_WIDTH;
        private int height = DEFAULT_HEIGHT;
        private Color background = DEFAULT_BACKGROUND;
        private int padding = DEFAULT_PADDING;
        private boolean isSpacer = false;

        public GroupComponent() {
            super("Group");
        }

        public void setDimensions(int width, int height) {
            this.width = width;
            this.height = height;
            // Wenn Breite 0 ist, ist es ein Spacer
            this.isSpacer = (width == 0);
        }

        public void setBackground(Color background) {
            this.background = background;
        }

        public void setPadding(int padding) {
            this.padding = padding;
        }

        @Override
        public void render(Graphics2D g2d, int x, int y, double scale) {
            // Spacer werden nicht gezeichnet, nehmen aber Platz ein
            if (isSpacer) {
                return;
            }

            int scaledWidth = (int) (width * scale);
            int scaledHeight = (int) (height * scale);
            int scaledPadding = (int) (padding * scale);

            // Hintergrund zeichnen
            g2d.setColor(background);
            g2d.fillRoundRect(x, y, scaledWidth, scaledHeight, BORDER_RADIUS, BORDER_RADIUS);

            // Border zeichnen
            g2d.setColor(BORDER_COLOR);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(x, y, scaledWidth, scaledHeight, BORDER_RADIUS, BORDER_RADIUS);

            // Berechne verfügbare Breite für Kinder
            int availableWidth = width - 2 * padding;

            // Kinder rendern mit korrekten Höhen
            int childY = y + scaledPadding;
            for (UIComponent child : children) {
                // Setze Breite für Komponenten die sie brauchen
                setChildWidth(child, availableWidth);
                child.render(g2d, x + scaledPadding, childY, scale);

                // Berechne die tatsächliche Höhe der Child-Komponente
                int childHeight = getComponentHeight(child, scale);
                childY += childHeight;
            }
        }

        private void setChildWidth(UIComponent child, int width) {
            if (child instanceof LabelComponent label) {
                label.setWidth(width);
            } else if (child instanceof ButtonComponent button) {
                button.setWidth(width);
            } else if (child instanceof TextFieldComponent textField) {
                textField.setWidth(width);
            }
        }

        private int getComponentHeight(UIComponent component, double scale) {
            if (component instanceof LabelComponent label) {
                return (int) (label.getHeight() * scale);
            } else if (component instanceof ButtonComponent button) {
                return (int) (button.getHeight() * scale);
            } else if (component instanceof TextFieldComponent textField) {
                return (int) (textField.getHeight() * scale);
            } else if (component instanceof GroupComponent group) {
                // Spacer Groups (Groups ohne Eigenschaften außer Anchor)
                return (int) (group.getHeight() * scale);
            }
            return (int) (DEFAULT_COMPONENT_HEIGHT * scale); // Default
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    /**
     * Label-Komponente
     */
    public static class LabelComponent extends UIComponent {
        private static final int DEFAULT_FONT_SIZE = 12;
        private static final int DEFAULT_HEIGHT = 30;
        private static final int DEFAULT_WIDTH = 300;
        private static final String DEFAULT_ALIGNMENT = "Left";
        private static final String FONT_NAME = "Segoe UI";

        private static final JBColor DEFAULT_TEXT_COLOR = JBColor.WHITE;

        private String text = "";
        private int fontSize = DEFAULT_FONT_SIZE;
        private Color textColor = DEFAULT_TEXT_COLOR;
        private boolean bold = false;
        private String alignment = DEFAULT_ALIGNMENT;
        private int height = DEFAULT_HEIGHT;
        private int width = DEFAULT_WIDTH;

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
            int scaledFontSize = (int) (fontSize * scale);
            int scaledHeight = (int) (height * scale);
            int scaledWidth = (int) (width * scale);

            Font font = new Font(FONT_NAME, bold ? Font.BOLD : Font.PLAIN, scaledFontSize);
            g2d.setFont(font);
            g2d.setColor(textColor);

            // Anti-Aliasing für bessere Textdarstellung
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);

            // Berechne X-Position basierend auf Alignment
            int textX = calculateTextX(x, scaledWidth, textWidth);

            // Vertikale Zentrierung innerhalb der Höhe
            int textY = y + ((scaledHeight - fm.getHeight()) / 2) + fm.getAscent();

            g2d.drawString(text, textX, textY);
        }

        private int calculateTextX(int x, int scaledWidth, int textWidth) {
            if ("Center".equalsIgnoreCase(alignment)) {
                return x + (scaledWidth - textWidth) / 2;
            } else if ("Right".equalsIgnoreCase(alignment)) {
                return x + scaledWidth - textWidth;
            }
            return x; // Left alignment (default)
        }
    }

    /**
     * Button-Komponente
     */
    public static class ButtonComponent extends UIComponent {
        private static final int DEFAULT_WIDTH = 150;
        private static final int DEFAULT_HEIGHT = 35;
        private static final int BORDER_RADIUS = 8;
        private static final int DEFAULT_FONT_SIZE = 14;
        private static final String FONT_NAME = "Segoe UI";

        private static final JBColor DEFAULT_BACKGROUND = new JBColor(new Color(0, 102, 204), new Color(0, 102, 204));
        private static final JBColor DEFAULT_TEXT_COLOR = JBColor.WHITE;

        private String text = "";
        private Color background = DEFAULT_BACKGROUND;
        private Color textColor = DEFAULT_TEXT_COLOR;
        private int width = DEFAULT_WIDTH;
        private int height = DEFAULT_HEIGHT;

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
            int scaledWidth = (int) (width * scale);
            int scaledHeight = (int) (height * scale);

            // Button Hintergrund
            g2d.setColor(background);
            g2d.fillRoundRect(x, y, scaledWidth, scaledHeight, BORDER_RADIUS, BORDER_RADIUS);

            // Button Border (hellerer)
            g2d.setColor(background.brighter());
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(x, y, scaledWidth, scaledHeight, BORDER_RADIUS, BORDER_RADIUS);

            // Text zentriert
            renderCenteredText(g2d, x, y, scaledWidth, scaledHeight, scale);
        }

        private void renderCenteredText(Graphics2D g2d, int x, int y, int scaledWidth, int scaledHeight, double scale) {
            Font font = new Font(FONT_NAME, Font.BOLD, (int) (DEFAULT_FONT_SIZE * scale));
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
        private static final int DEFAULT_WIDTH = 200;
        private static final int DEFAULT_HEIGHT = 30;
        private static final int BORDER_RADIUS = 6;
        private static final int TEXT_PADDING = 8;
        private static final int DEFAULT_FONT_SIZE = 11;
        private static final String FONT_NAME = "Segoe UI";

        private static final JBColor DEFAULT_BACKGROUND = new JBColor(new Color(45, 45, 68), new Color(45, 45, 68));
        private static final JBColor BORDER_COLOR = new JBColor(new Color(70, 70, 90), new Color(70, 70, 90));
        private static final JBColor PLACEHOLDER_COLOR = new JBColor(Gray._150, Gray._150);

        private String placeholderText = "";
        private Color background = DEFAULT_BACKGROUND;
        private int width = DEFAULT_WIDTH;
        private int height = DEFAULT_HEIGHT;

        public TextFieldComponent() {
            super("TextField");
        }

        public void setPlaceholderText(String placeholderText) {
            this.placeholderText = placeholderText;
        }

        public void setBackground(Color background) {
            this.background = background;
        }

        @SuppressWarnings("unused")
        public void setTextColor(Color textColor) {
            // Methode behalten für API-Kompatibilität, aber nicht mehr verwendet
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
            int scaledWidth = (int) (width * scale);
            int scaledHeight = (int) (height * scale);

            // TextField Hintergrund
            g2d.setColor(background);
            g2d.fillRoundRect(x, y, scaledWidth, scaledHeight, BORDER_RADIUS, BORDER_RADIUS);

            // Border
            g2d.setColor(BORDER_COLOR);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(x, y, scaledWidth, scaledHeight, BORDER_RADIUS, BORDER_RADIUS);

            // Placeholder Text
            renderPlaceholderText(g2d, x, y, scaledHeight, scale);
        }

        private void renderPlaceholderText(Graphics2D g2d, int x, int y, int scaledHeight, double scale) {
            Font font = new Font(FONT_NAME, Font.ITALIC, (int) (DEFAULT_FONT_SIZE * scale));
            g2d.setFont(font);
            g2d.setColor(PLACEHOLDER_COLOR);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            FontMetrics fm = g2d.getFontMetrics();
            int textY = y + ((scaledHeight - fm.getHeight()) / 2) + fm.getAscent();

            g2d.drawString(placeholderText, x + (int) (TEXT_PADDING * scale), textY);
        }
    }
}

