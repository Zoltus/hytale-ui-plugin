package de.bungee.uifile.preview;

import com.intellij.ui.JBColor;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UIModelParser {
    private static final Pattern LABEL_PATTERN = Pattern.compile("Label\\s*(?:#\\w+)?\\s*\\{([^}]+)}", Pattern.DOTALL);
    private static final Pattern BUTTON_PATTERN = Pattern.compile("(?:Text)?Button\\s*(?:#\\w+)?\\s*\\{([^}]+)}",
        Pattern.DOTALL);
    private static final Pattern TEXTFIELD_PATTERN = Pattern.compile(
        "(?:\\$\\w+\\.)?@?TextField\\s*(?:#\\w+)?\\s*\\{([^}]+)}", Pattern.DOTALL);
    private static final Pattern ANCHOR_PATTERN = Pattern.compile(
        "Anchor:\\s*\\(\\s*(?:Width:\\s*(\\d+)\\s*,?\\s*)?(?:Height:\\s*(\\d+))?\\s*\\)");
    private static final Pattern BACKGROUND_PATTERN = Pattern.compile(
        "Background:\\s*(#[0-9a-fA-F]{6})(?:\\(([0-9.]+)\\))?");
    private static final Pattern PADDING_PATTERN = Pattern.compile("Padding:\\s*\\(\\s*Full:\\s*(\\d+)\\s*\\)");
    private static final Pattern TEXT_PATTERN = Pattern.compile("Text:\\s*\"([^\"]+)\"");
    private static final Pattern STYLE_PATTERN = Pattern.compile("Style:\\s*(?:@\\w+|\\(([^)]+)\\))");
    private static final Pattern FONTSIZE_PATTERN = Pattern.compile("FontSize:\\s*(\\d+)");
    private static final Pattern TEXTCOLOR_PATTERN = Pattern.compile("TextColor:\\s*(#[0-9a-fA-F]{6})");
    private static final Pattern HORIZONTAL_ALIGNMENT_PATTERN = Pattern.compile(
        "(?:HorizontalAlignment|Alignment):\\s*(\\w+)");
    private static final Pattern RENDER_BOLD_PATTERN = Pattern.compile("RenderBold:\\s*(true|false)");
    private static final Pattern RENDER_UPPERCASE_PATTERN = Pattern.compile("RenderUppercase:\\s*(true|false)");
    private static final Pattern LETTER_SPACING_PATTERN = Pattern.compile("LetterSpacing:\\s*(\\d+(?:\\.\\d+)?)");
    private static final Pattern LAYOUTMODE_PATTERN = Pattern.compile("LayoutMode:\\s*(\\w+)");
    private static final Pattern FLEXWEIGHT_PATTERN = Pattern.compile("FlexWeight:\\s*(\\d+(?:\\.\\d+)?)");

    public static UIModel parse(String content) {
        UIModel model = new UIModel();
        if (content == null || content.trim().isEmpty()) {
            return model;
        }

        int pos = 0;
        while (pos < content.length()) {
            int groupStart = content.indexOf("Group", pos);
            if (groupStart == -1) {
                break;
            }
            int braceStart = content.indexOf("{", groupStart);
            if (braceStart == -1) {
                break;
            }
            int braceEnd = findMatchingBrace(content, braceStart);
            if (braceEnd == -1) {
                break;
            }

            String groupContent = content.substring(braceStart + 1, braceEnd);
            UIModel.GroupComponent group = parseGroup(groupContent);

            if (group.getPreferredWidth() == 0) {
                group.setDimensions(500, 320);
            }

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
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
            }
            pos++;
        }
        return braceCount == 0 ? pos - 1 : -1;
    }

    private static UIModel.GroupComponent parseGroup(String content) {
        UIModel.GroupComponent group = new UIModel.GroupComponent();
        Matcher lm = LAYOUTMODE_PATTERN.matcher(content);
        if (lm.find()) {
            group.setLayoutMode(lm.group(1));
        }
        Matcher fm = FLEXWEIGHT_PATTERN.matcher(content);
        if (fm.find()) {
            group.setFlexWeight(Float.parseFloat(fm.group(1)));
        }
        Matcher am = ANCHOR_PATTERN.matcher(content);
        if (am.find()) {
            int w = am.group(1) != null ? Integer.parseInt(am.group(1)) : 0;
            int h = am.group(2) != null ? Integer.parseInt(am.group(2)) : 0;
            group.setDimensions(w, h);
        }
        Matcher bgm = BACKGROUND_PATTERN.matcher(content);
        if (bgm.find()) {
            float alpha = bgm.group(2) != null ? Float.parseFloat(bgm.group(2)) : 1.0f;
            group.setBackground(parseColor(bgm.group(1), alpha));
        }
        Matcher pm = PADDING_PATTERN.matcher(content);
        if (pm.find()) {
            group.setPadding(Integer.parseInt(pm.group(1)));
        }

        parseChildComponents(content, group);
        return group;
    }

    private static void parseChildComponents(String content, UIModel.GroupComponent parent) {
        List<ComponentPosition> components = new ArrayList<>();
        int pos = 0;
        while (pos < content.length()) {
            int groupStart = content.indexOf("Group", pos);
            if (groupStart == -1) {
                break;
            }
            int braceStart = content.indexOf("{", groupStart);
            if (braceStart == -1) {
                break;
            }
            int braceEnd = findMatchingBrace(content, braceStart);
            if (braceEnd == -1) {
                break;
            }
            components.add(
                new ComponentPosition(groupStart, braceEnd + 1, "Group", content.substring(braceStart + 1, braceEnd)));
            pos = braceEnd + 1;
        }

        addIfNotNested(LABEL_PATTERN.matcher(content), "Label", components);
        addIfNotNested(BUTTON_PATTERN.matcher(content), "Button", components);
        addIfNotNested(TEXTFIELD_PATTERN.matcher(content), "TextField", components);
        components.sort(Comparator.comparingInt(cp -> cp.position));

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
                case "Group":
                    parent.addChild(parseGroup(cp.content));
                    break;
            }
        }
    }

    private static void addIfNotNested(Matcher matcher, String type, List<ComponentPosition> components) {
        while (matcher.find()) {
            int start = matcher.start();
            boolean nested = false;
            for (ComponentPosition cp : components) {
                if (cp.type.equals("Group") && start > cp.position && start < cp.endPosition) {
                    nested = true;
                    break;
                }
            }
            if (!nested) {
                components.add(new ComponentPosition(start, matcher.end(), type, matcher.group(1)));
            }
        }
    }

    private static UIModel.LabelComponent parseLabel(String content) {
        UIModel.LabelComponent label = new UIModel.LabelComponent();
        Matcher tm = TEXT_PATTERN.matcher(content);
        if (tm.find()) {
            label.setText(tm.group(1));
        }
        Matcher am = ANCHOR_PATTERN.matcher(content);
        if (am.find()) {
            label.setDimensions(
                am.group(1) != null ? Integer.parseInt(am.group(1)) : 0,
                am.group(2) != null ? Integer.parseInt(am.group(2)) : 0
            );
        }
        Matcher sm = STYLE_PATTERN.matcher(content);
        if (sm.find() && sm.group(1) != null) {
            String style = sm.group(1);
            Matcher fs = FONTSIZE_PATTERN.matcher(style);
            if (fs.find()) {
                label.setFontSize(Integer.parseInt(fs.group(1)));
            }
            Matcher tc = TEXTCOLOR_PATTERN.matcher(style);
            if (tc.find()) {
                label.setTextColor(parseColor(tc.group(1), 1.0f));
            }
            Matcher rb = RENDER_BOLD_PATTERN.matcher(style);
            if (rb.find()) {
                label.setBold(Boolean.parseBoolean(rb.group(1)));
            }
            Matcher ru = RENDER_UPPERCASE_PATTERN.matcher(style);
            if (ru.find()) {
                label.setUppercase(Boolean.parseBoolean(ru.group(1)));
            }
            Matcher ls = LETTER_SPACING_PATTERN.matcher(style);
            if (ls.find()) {
                label.setLetterSpacing(Float.parseFloat(ls.group(1)));
            }
            Matcher al = HORIZONTAL_ALIGNMENT_PATTERN.matcher(style);
            if (al.find()) {
                label.setAlignment(al.group(1));
            }
        }
        return label;
    }

    private static UIModel.ButtonComponent parseButton(String content) {
        UIModel.ButtonComponent button = new UIModel.ButtonComponent();
        Matcher tm = TEXT_PATTERN.matcher(content);
        if (tm.find()) {
            button.setText(tm.group(1));
        }
        Matcher am = ANCHOR_PATTERN.matcher(content);
        if (am.find()) {
            button.setDimensions(
                am.group(1) != null ? Integer.parseInt(am.group(1)) : 100,
                am.group(2) != null ? Integer.parseInt(am.group(2)) : 30
            );
        }
        Matcher bgm = BACKGROUND_PATTERN.matcher(content);
        if (bgm.find()) {
            button.setBackground(parseColor(bgm.group(1), 1.0f));
        }
        return button;
    }

    private static UIModel.TextFieldComponent parseTextField(String content) {
        UIModel.TextFieldComponent field = new UIModel.TextFieldComponent();
        Matcher am = ANCHOR_PATTERN.matcher(content);
        if (am.find()) {
            field.setDimensions(
                am.group(1) != null ? Integer.parseInt(am.group(1)) : 200,
                am.group(2) != null ? Integer.parseInt(am.group(2)) : 30
            );
        }
        return field;
    }

    private static Color parseColor(String hex, float alpha) {
        try {
            if (hex.startsWith("#")) {
                hex = hex.substring(1);
            }
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            int a = (int) (alpha * 255);
            return new JBColor(new Color(r, g, b, a), new Color(r, g, b, a));
        } catch (Exception e) {
            return JBColor.WHITE;
        }
    }

    private static class ComponentPosition {
        int position, endPosition;
        String type, content;

        ComponentPosition(int p, int ep, String t, String c) {
            this.position = p;
            this.endPosition = ep;
            this.type = t;
            this.content = c;
        }
    }
}