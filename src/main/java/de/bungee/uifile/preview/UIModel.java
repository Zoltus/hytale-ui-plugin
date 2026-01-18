package de.bungee.uifile.preview;

import com.intellij.ui.JBColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UIModel {
    private final List<GroupComponent> topLevelGroups = new ArrayList<>();

    public void addComponent(GroupComponent group) {
        topLevelGroups.add(group);
    }

    public List<GroupComponent> getTopLevelGroups() {
        return topLevelGroups;
    }

    public static class Component {
        protected int x, y, width, height;
        protected int prefWidth, prefHeight;
        protected float flexWeight = 0;
        protected Color background;

        public void setDimensions(int w, int h) {
            this.prefWidth = w;
            this.prefHeight = h;
        }

        public int getPreferredWidth() {
            return prefWidth;
        }

        public int getPreferredHeight() {
            return prefHeight;
        }

        public void setFlexWeight(float f) {
            this.flexWeight = f;
        }

        public void setBackground(Color c) {
            this.background = c;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }

        public void setBounds(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
        }
    }

    public static class GroupComponent extends Component {
        private String layoutMode = "Top";
        private int padding = 0;
        private final List<Component> children = new ArrayList<>();

        public void addChild(Component child) {
            children.add(child);
        }

        public List<Component> getChildren() {
            return children;
        }

        public void setLayoutMode(String mode) {
            this.layoutMode = mode;
        }

        public String getLayoutMode() {
            return layoutMode;
        }

        public void setPadding(int p) {
            this.padding = p;
        }

        public int getPadding() {
            return padding;
        }
    }

    public static class LabelComponent extends Component {
        private String text = "";
        private int fontSize = 12;
        private Color textColor = JBColor.WHITE;
        private boolean bold = false;
        private boolean uppercase = false;
        private float letterSpacing = 0;
        private String alignment = "Left";

        public void setText(String t) {
            this.text = t;
        }

        public String getText() {
            return uppercase ? text.toUpperCase() : text;
        }

        public void setFontSize(int s) {
            this.fontSize = s;
        }

        public int getFontSize() {
            return fontSize;
        }

        public void setTextColor(Color c) {
            this.textColor = c;
        }

        public Color getTextColor() {
            return textColor;
        }

        public void setBold(boolean b) {
            this.bold = b;
        }

        public boolean isBold() {
            return bold;
        }

        public void setUppercase(boolean u) {
            this.uppercase = u;
        }

        public void setLetterSpacing(float s) {
            this.letterSpacing = s;
        }

        public float getLetterSpacing() {
            return letterSpacing;
        }

        public void setAlignment(String a) {
            this.alignment = a;
        }

        public String getAlignment() {
            return alignment;
        }
    }

    public static class ButtonComponent extends Component {
        private String text = "";

        public void setText(String t) {
            this.text = t;
        }

        public String getText() {
            return text;
        }
    }

    public static class TextFieldComponent extends Component {
        private String placeholder = "";

        public void setPlaceholder(String p) {
            this.placeholder = p;
        }

        public String getPlaceholder() {
            return placeholder;
        }
    }
}