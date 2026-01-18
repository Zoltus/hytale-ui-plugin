package de.bungee.uifile.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UILexer extends LexerBase {
    private CharSequence buffer;
    private int startOffset;
    private int endOffset;
    private int currentOffset;
    private IElementType currentTokenType;
    private int currentTokenEnd;

    // Token Types
    public static final UITokenType COMPONENT = new UITokenType("COMPONENT");
    public static final UITokenType PROPERTY = new UITokenType("PROPERTY");
    public static final UITokenType STRING = new UITokenType("STRING");
    public static final UITokenType NUMBER = new UITokenType("NUMBER");
    public static final UITokenType COLOR = new UITokenType("COLOR");
    public static final UITokenType IDENTIFIER = new UITokenType("IDENTIFIER");
    public static final UITokenType LBRACE = new UITokenType("LBRACE");
    public static final UITokenType RBRACE = new UITokenType("RBRACE");
    public static final UITokenType LPAREN = new UITokenType("LPAREN");
    public static final UITokenType RPAREN = new UITokenType("RPAREN");
    public static final UITokenType COLON = new UITokenType("COLON");
    public static final UITokenType SEMICOLON = new UITokenType("SEMICOLON");
    public static final UITokenType COMMA = new UITokenType("COMMA");
    public static final UITokenType HASH = new UITokenType("HASH");
    public static final UITokenType AT = new UITokenType("AT");
    public static final UITokenType EQUALS = new UITokenType("EQUALS");
    public static final UITokenType DOLLAR = new UITokenType("DOLLAR");
    public static final UITokenType DOT = new UITokenType("DOT");
    public static final UITokenType COMMENT = new UITokenType("COMMENT");
    public static final UITokenType WHITE_SPACE = new UITokenType("WHITE_SPACE");
    public static final UITokenType BAD_CHARACTER = new UITokenType("BAD_CHARACTER");

    // Known Components - erweitert basierend auf den UI-Dateien
    private static final String[] COMPONENTS = {
        // Standard UI Components
        "Group", "Label", "Button", "Input", "Panel", "Container",

        // Item/Inventory Components
        "ItemIcon", "ItemSlot", "ItemSlotButton", "ItemGrid",

        // Interactive Components
        "TextButton", "Slider", "FloatSlider", "CheckBox", "DropdownBox",
        "TextField", "NumberField",

        // Specialized Components (können vom Entwickler definiert sein)
        "DecoratedContainer", "PageOverlay"
    };

    // Known Properties - stark erweitert basierend auf den UI-Dateien
    private static final String[] PROPERTIES = {
        // Layout & Positioning
        "Anchor", "LayoutMode", "Padding", "FlexWeight",

        // Size Properties
        "Width", "Height", "Full",

        // Directional Anchor Properties
        "Top", "Bottom", "Left", "Right",
        "Horizontal", "Vertical",

        // Alignment
        "Alignment", "HorizontalAlignment", "VerticalAlignment",
        "Center", "Start", "End",

        // Visual Properties
        "Background", "Border", "Color", "TexturePath",
        "Visible", "HitTestVisible",

        // Text & Font Properties
        "Text", "TextColor", "FontSize",
        "RenderBold", "RenderUppercase", "Wrap",
        "PlaceholderText", "MaxLength",

        // Style Properties
        "Style", "Default", "Hovered", "Pressed", "Disabled",
        "LabelStyle", "ScrollbarStyle", "Sounds",

        // Item Slot Specific
        "ShowQualityBackground", "ShowQuantity",
        "SlotSize", "SlotIconSize", "SlotSpacing", "SlotBackground",
        "SlotsPerRow",

        // Slider Properties
        "Min", "Max", "Step", "Value",

        // Number Field Properties
        "Format", "MaxDecimalPlaces", "MinValue", "MaxValue"
    };

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.currentOffset = startOffset;
        advance();
    }

    @Override
    public int getState() {
        return 0;
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        return currentTokenType;
    }

    @Override
    public int getTokenStart() {
        return startOffset;
    }

    @Override
    public int getTokenEnd() {
        return currentTokenEnd;
    }

    @Override
    public void advance() {
        if (currentOffset >= endOffset) {
            currentTokenType = null;
            return;
        }

        startOffset = currentOffset;
        char c = buffer.charAt(currentOffset);

        // Whitespace
        if (Character.isWhitespace(c)) {
            currentTokenType = WHITE_SPACE;
            while (currentOffset < endOffset && Character.isWhitespace(buffer.charAt(currentOffset))) {
                currentOffset++;
            }
            currentTokenEnd = currentOffset;
            return;
        }

        // Comments (// style)
        if (c == '/' && currentOffset + 1 < endOffset && buffer.charAt(currentOffset + 1) == '/') {
            currentTokenType = COMMENT;
            while (currentOffset < endOffset && buffer.charAt(currentOffset) != '\n') {
                currentOffset++;
            }
            currentTokenEnd = currentOffset;
            return;
        }

        // Strings
        if (c == '"') {
            currentTokenType = STRING;
            currentOffset++;
            while (currentOffset < endOffset) {
                char ch = buffer.charAt(currentOffset);
                currentOffset++;
                if (ch == '"') {
                    break;
                }
                if (ch == '\\' && currentOffset < endOffset) {
                    currentOffset++;
                }
            }
            currentTokenEnd = currentOffset;
            return;
        }

        // Hex colors
        if (c == '#') {
            currentTokenType = HASH;
            currentOffset++;
            int hexStart = currentOffset;
            while (currentOffset < endOffset && isHexDigit(buffer.charAt(currentOffset))) {
                currentOffset++;
            }
            if (currentOffset - hexStart >= 3) { // At least 3 hex digits
                currentTokenType = COLOR;
            }
            currentTokenEnd = currentOffset;
            return;
        }

        // Numbers (including negative numbers and decimals)
        if (Character.isDigit(c) || (c == '-' && currentOffset + 1 < endOffset && Character.isDigit(
            buffer.charAt(currentOffset + 1)))) {
            currentTokenType = NUMBER;
            if (c == '-') {
                currentOffset++;
            }
            while (currentOffset < endOffset && (Character.isDigit(buffer.charAt(currentOffset))
                                                 || buffer.charAt(currentOffset) == '.')) {
                currentOffset++;
            }
            currentTokenEnd = currentOffset;
            return;
        }

        // Single-character tokens
        switch (c) {
            case '{':
                currentTokenType = LBRACE;
                currentOffset++;
                currentTokenEnd = currentOffset;
                return;
            case '}':
                currentTokenType = RBRACE;
                currentOffset++;
                currentTokenEnd = currentOffset;
                return;
            case '(':
                currentTokenType = LPAREN;
                currentOffset++;
                currentTokenEnd = currentOffset;
                return;
            case ')':
                currentTokenType = RPAREN;
                currentOffset++;
                currentTokenEnd = currentOffset;
                return;
            case ':':
                currentTokenType = COLON;
                currentOffset++;
                currentTokenEnd = currentOffset;
                return;
            case ';':
                currentTokenType = SEMICOLON;
                currentOffset++;
                currentTokenEnd = currentOffset;
                return;
            case ',':
                currentTokenType = COMMA;
                currentOffset++;
                currentTokenEnd = currentOffset;
                return;
            case '@':
                currentTokenType = AT;
                currentOffset++;
                currentTokenEnd = currentOffset;
                return;
            case '=':
                currentTokenType = EQUALS;
                currentOffset++;
                currentTokenEnd = currentOffset;
                return;
            case '$':
                currentTokenType = DOLLAR;
                currentOffset++;
                currentTokenEnd = currentOffset;
                return;
            case '.':
                currentTokenType = DOT;
                currentOffset++;
                currentTokenEnd = currentOffset;
                return;
        }

        // Identifiers (Components and Properties)
        if (Character.isJavaIdentifierStart(c)) {
            StringBuilder identifier = new StringBuilder();
            while (currentOffset < endOffset && (Character.isJavaIdentifierPart(buffer.charAt(currentOffset))
                                                 || buffer.charAt(currentOffset) == '_')) {
                identifier.append(buffer.charAt(currentOffset));
                currentOffset++;
            }

            String word = identifier.toString();

            // Check if it's a component
            for (String comp : COMPONENTS) {
                if (comp.equals(word)) {
                    currentTokenType = COMPONENT;
                    currentTokenEnd = currentOffset;
                    return;
                }
            }

            // Check if it's a property
            for (String prop : PROPERTIES) {
                if (prop.equals(word)) {
                    currentTokenType = PROPERTY;
                    currentTokenEnd = currentOffset;
                    return;
                }
            }

            currentTokenType = IDENTIFIER;
            currentTokenEnd = currentOffset;
            return;
        }

        // Unknown character
        currentTokenType = BAD_CHARACTER;
        currentOffset++;
        currentTokenEnd = currentOffset;
    }

    private boolean isHexDigit(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return buffer;
    }

    @Override
    public int getBufferEnd() {
        return endOffset;
    }
}