package de.bungee.uifile.preview;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser für .ui-Dateien, der den Text in ein UI-Modell umwandelt
 */
public class UIModelParser {

    private static final Pattern GROUP_PATTERN = Pattern.compile("Group\\s*(?:#\\w+)?\\s*\\{", Pattern.DOTALL);
    private static final Pattern LABEL_PATTERN = Pattern.compile("Label\\s*(?:#\\w+)?\\s*\\{([^}]+)\\}", Pattern.DOTALL);
    private static final Pattern BUTTON_PATTERN = Pattern.compile("(?:Text)?Button\\s*(?:#\\w+)?\\s*\\{([^}]+)\\}", Pattern.DOTALL);
    private static final Pattern TEXTFIELD_PATTERN = Pattern.compile("(?:\\$\\w+\\.)?(?:@)?TextField\\s*(?:#\\w+)?\\s*\\{([^}]+)\\}", Pattern.DOTALL);

    private static final Pattern ANCHOR_PATTERN = Pattern.compile("Anchor:\\s*\\(\\s*(?:Width:\\s*(\\d+)\\s*,?\\s*)?(?:Height:\\s*(\\d+))?\\s*\\)");
    private static final Pattern BACKGROUND_PATTERN = Pattern.compile("Background:\\s*(#[0-9a-fA-F]{6})(?:\\([0-9.]+\\))?");
    private static final Pattern PADDING_PATTERN = Pattern.compile("Padding:\\s*\\(\\s*Full:\\s*(\\d+)\\s*\\)");
    private static final Pattern TEXT_PATTERN = Pattern.compile("Text:\\s*\"([^\"]+)\"");
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("PlaceholderText:\\s*\"([^\"]+)\"");
    private static final Pattern STYLE_PATTERN = Pattern.compile("Style:\\s*(?:@\\w+|\\(([^)]+)\\))");
    private static final Pattern FONTSIZE_PATTERN = Pattern.compile("FontSize:\\s*(\\d+)");
    private static final Pattern TEXTCOLOR_PATTERN = Pattern.compile("TextColor:\\s*(#[0-9a-fA-F]{6})");
    private static final Pattern HORIZONTAL_ALIGNMENT_PATTERN = Pattern.compile("HorizontalAlignment:\\s*(\\w+)");
    private static final Pattern RENDER_BOLD_PATTERN = Pattern.compile("RenderBold:\\s*(true|false)");

    public static UIModel parse(String content) {
        UIModel model = new UIModel();

        if (content == null || content.trim().isEmpty()) {
            return model;
        }

        // Parse Groups - finde alle Groups mit ihrem kompletten Inhalt
        int pos = 0;
        while (pos < content.length()) {
            int groupStart = content.indexOf("Group", pos);
            if (groupStart == -1) break;

            // Finde die öffnende Klammer
            int braceStart = content.indexOf("{", groupStart);
            if (braceStart == -1) break;

            // Finde die schließende Klammer (mit Bracket-Counting für verschachtelte Strukturen)
            int braceEnd = findMatchingBrace(content, braceStart);
            if (braceEnd == -1) break;

            String groupContent = content.substring(braceStart + 1, braceEnd);
            UIModel.GroupComponent group = parseGroup(groupContent);
            model.addComponent(group);

            pos = braceEnd + 1;
        }

        return model;
    }

    private static int findMatchingBrace(String content, int openBracePos) {
        int braceCount = 1;
        int pos = openBracePos + 1;

        while (pos < content.length() && braceCount > 0) {
            char c = content.charAt(pos);
            if (c == '{') braceCount++;
            else if (c == '}') braceCount--;
            pos++;
        }

        return braceCount == 0 ? pos - 1 : -1;
    }

    private static UIModel.GroupComponent parseGroup(String content) {
        UIModel.GroupComponent group = new UIModel.GroupComponent();

        // Parse Anchor (Dimensionen)
        Matcher anchorMatcher = ANCHOR_PATTERN.matcher(content);
        if (anchorMatcher.find()) {
            String widthStr = anchorMatcher.group(1);
            String heightStr = anchorMatcher.group(2);

            int width = widthStr != null ? Integer.parseInt(widthStr) : 350;
            int height = heightStr != null ? Integer.parseInt(heightStr) : 180;
            group.setDimensions(width, height);
        }

        // Parse Background
        Matcher bgMatcher = BACKGROUND_PATTERN.matcher(content);
        if (bgMatcher.find()) {
            Color bg = parseColor(bgMatcher.group(1));
            if (bg != null) {
                group.setBackground(bg);
            }
        }

        // Parse Padding
        Matcher paddingMatcher = PADDING_PATTERN.matcher(content);
        if (paddingMatcher.find()) {
            int padding = Integer.parseInt(paddingMatcher.group(1));
            group.setPadding(padding);
        }

        // Parse Child Components
        parseChildComponents(content, group);

        return group;
    }

    private static void parseChildComponents(String content, UIModel.GroupComponent parent) {
        List<ComponentPosition> components = new ArrayList<>();

        // Finde alle Labels
        Matcher labelMatcher = LABEL_PATTERN.matcher(content);
        while (labelMatcher.find()) {
            components.add(new ComponentPosition(labelMatcher.start(), "Label", labelMatcher.group(1)));
        }

        // Finde alle Buttons (inkl. TextButton)
        Matcher buttonMatcher = BUTTON_PATTERN.matcher(content);
        while (buttonMatcher.find()) {
            components.add(new ComponentPosition(buttonMatcher.start(), "Button", buttonMatcher.group(1)));
        }

        // Finde alle TextFields
        Matcher textFieldMatcher = TEXTFIELD_PATTERN.matcher(content);
        while (textFieldMatcher.find()) {
            components.add(new ComponentPosition(textFieldMatcher.start(), "TextField", textFieldMatcher.group(1)));
        }

        // Finde alle Spacer Groups
        int pos = 0;
        while (pos < content.length()) {
            int groupStart = content.indexOf("Group", pos);
            if (groupStart == -1) break;

            int braceStart = content.indexOf("{", groupStart);
            if (braceStart == -1) break;

            int braceEnd = findMatchingBrace(content, braceStart);
            if (braceEnd == -1) break;

            String groupContent = content.substring(braceStart + 1, braceEnd);
            // Nur als Spacer verwenden, wenn es eine einfache Group ist
            if (groupContent.trim().matches("Anchor:\\s*\\(\\s*Height:\\s*\\d+\\s*\\);?\\s*")) {
                components.add(new ComponentPosition(groupStart, "Spacer", groupContent));
            }

            pos = braceEnd + 1;
        }

        // Sortiere nach Position im Dokument
        components.sort((a, b) -> Integer.compare(a.position, b.position));

        // Parse und füge Komponenten in der richtigen Reihenfolge hinzu
        for (ComponentPosition cp : components) {
            switch (cp.type) {
                case "Label":
                    parent.addChild(parseLabel(cp.content));
                    break;
                case "Button":
                    parent.addChild(parseButton(cp.content));
                    break;
                case "TextField":
                    parent.addChild(parseTextField(cp.content));
                    break;
                case "Spacer":
                    UIModel.GroupComponent spacer = new UIModel.GroupComponent();
                    Matcher anchorMatcher = ANCHOR_PATTERN.matcher(cp.content);
                    if (anchorMatcher.find()) {
                        String heightStr = anchorMatcher.group(2);
                        if (heightStr != null) {
                            spacer.setDimensions(0, Integer.parseInt(heightStr));
                        }
                    }
                    parent.addChild(spacer);
                    break;
            }
        }
    }

    // Hilfsklasse zum Speichern der Position von Komponenten
    private static class ComponentPosition {
        int position;
        String type;
        String content;

        ComponentPosition(int position, String type, String content) {
            this.position = position;
            this.type = type;
            this.content = content;
        }
    }

    private static UIModel.LabelComponent parseLabel(String content) {
        UIModel.LabelComponent label = new UIModel.LabelComponent();

        // Parse Text
        Matcher textMatcher = TEXT_PATTERN.matcher(content);
        if (textMatcher.find()) {
            label.setText(textMatcher.group(1));
        }

        // Parse Anchor für Höhe
        Matcher anchorMatcher = ANCHOR_PATTERN.matcher(content);
        if (anchorMatcher.find()) {
            String heightStr = anchorMatcher.group(2);
            if (heightStr != null) {
                label.setHeight(Integer.parseInt(heightStr));
            }
        }

        // Parse Style
        Matcher styleMatcher = STYLE_PATTERN.matcher(content);
        if (styleMatcher.find()) {
            String styleContent = styleMatcher.group(1);
            if (styleContent != null) {
                // Parse FontSize
                Matcher fontSizeMatcher = FONTSIZE_PATTERN.matcher(styleContent);
                if (fontSizeMatcher.find()) {
                    label.setFontSize(Integer.parseInt(fontSizeMatcher.group(1)));
                }

                // Parse TextColor
                Matcher textColorMatcher = TEXTCOLOR_PATTERN.matcher(styleContent);
                if (textColorMatcher.find()) {
                    Color color = parseColor(textColorMatcher.group(1));
                    if (color != null) {
                        label.setTextColor(color);
                    }
                }

                // Parse RenderBold
                Matcher boldMatcher = RENDER_BOLD_PATTERN.matcher(styleContent);
                if (boldMatcher.find()) {
                    label.setBold(Boolean.parseBoolean(boldMatcher.group(1)));
                }

                // Parse HorizontalAlignment
                Matcher alignMatcher = HORIZONTAL_ALIGNMENT_PATTERN.matcher(styleContent);
                if (alignMatcher.find()) {
                    label.setAlignment(alignMatcher.group(1));
                }
            }
        }

        return label;
    }

    private static UIModel.ButtonComponent parseButton(String content) {
        UIModel.ButtonComponent button = new UIModel.ButtonComponent();

        // Parse Text
        Matcher textMatcher = TEXT_PATTERN.matcher(content);
        if (textMatcher.find()) {
            button.setText(textMatcher.group(1));
        }

        // Parse Anchor für Höhe
        Matcher anchorMatcher = ANCHOR_PATTERN.matcher(content);
        if (anchorMatcher.find()) {
            String heightStr = anchorMatcher.group(2);
            if (heightStr != null) {
                button.setHeight(Integer.parseInt(heightStr));
            }
        }

        // Parse Background
        Matcher bgMatcher = BACKGROUND_PATTERN.matcher(content);
        if (bgMatcher.find()) {
            Color bg = parseColor(bgMatcher.group(1));
            if (bg != null) {
                button.setBackground(bg);
            }
        }

        // Parse Style (für @SaveButtonStyle und andere)
        Matcher styleMatcher = STYLE_PATTERN.matcher(content);
        if (styleMatcher.find()) {
            String styleContent = styleMatcher.group(1);
            if (styleContent != null) {
                // Parse TextColor
                Matcher textColorMatcher = TEXTCOLOR_PATTERN.matcher(styleContent);
                if (textColorMatcher.find()) {
                    Color color = parseColor(textColorMatcher.group(1));
                    if (color != null) {
                        button.setTextColor(color);
                    }
                }
            }
        }

        return button;
    }

    private static UIModel.TextFieldComponent parseTextField(String content) {
        UIModel.TextFieldComponent textField = new UIModel.TextFieldComponent();

        // Parse PlaceholderText
        Matcher placeholderMatcher = PLACEHOLDER_PATTERN.matcher(content);
        if (placeholderMatcher.find()) {
            textField.setPlaceholderText(placeholderMatcher.group(1));
        }

        // Parse Anchor für Höhe
        Matcher anchorMatcher = ANCHOR_PATTERN.matcher(content);
        if (anchorMatcher.find()) {
            String heightStr = anchorMatcher.group(2);
            if (heightStr != null) {
                textField.setHeight(Integer.parseInt(heightStr));
            }
        }

        // Parse Background
        Matcher bgMatcher = BACKGROUND_PATTERN.matcher(content);
        if (bgMatcher.find()) {
            Color bg = parseColor(bgMatcher.group(1));
            if (bg != null) {
                textField.setBackground(bg);
            }
        }

        // Parse Style
        Matcher styleMatcher = STYLE_PATTERN.matcher(content);
        if (styleMatcher.find()) {
            String styleContent = styleMatcher.group(1);
            if (styleContent != null) {
                // Parse TextColor
                Matcher textColorMatcher = TEXTCOLOR_PATTERN.matcher(styleContent);
                if (textColorMatcher.find()) {
                    Color color = parseColor(textColorMatcher.group(1));
                    if (color != null) {
                        textField.setTextColor(color);
                    }
                }
            }
        }

        return textField;
    }

    private static Color parseColor(String hexColor) {
        try {
            if (hexColor.startsWith("#")) {
                hexColor = hexColor.substring(1);
            }

            if (hexColor.length() == 6) {
                // #RRGGBB
                int r = Integer.parseInt(hexColor.substring(0, 2), 16);
                int g = Integer.parseInt(hexColor.substring(2, 4), 16);
                int b = Integer.parseInt(hexColor.substring(4, 6), 16);
                return new Color(r, g, b);
            } else if (hexColor.length() == 8) {
                // #RRGGBBAA
                int r = Integer.parseInt(hexColor.substring(0, 2), 16);
                int g = Integer.parseInt(hexColor.substring(2, 4), 16);
                int b = Integer.parseInt(hexColor.substring(4, 6), 16);
                int a = Integer.parseInt(hexColor.substring(6, 8), 16);
                return new Color(r, g, b, a);
            }
        } catch (NumberFormatException e) {
            // Fehlerhafte Farbe - Default zurückgeben
        }
        return null;
    }
}

