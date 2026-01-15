package de.bungee.uifile.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class UIColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Component", UISyntaxHighlighter.COMPONENT),
            new AttributesDescriptor("Property", UISyntaxHighlighter.PROPERTY),
            new AttributesDescriptor("String", UISyntaxHighlighter.STRING),
            new AttributesDescriptor("Number", UISyntaxHighlighter.NUMBER),
            new AttributesDescriptor("Color", UISyntaxHighlighter.COLOR),
            new AttributesDescriptor("Identifier", UISyntaxHighlighter.IDENTIFIER),
            new AttributesDescriptor("Comment", UISyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Braces", UISyntaxHighlighter.BRACES),
            new AttributesDescriptor("Parentheses", UISyntaxHighlighter.PARENTHESES),
            new AttributesDescriptor("Colon", UISyntaxHighlighter.COLON),
            new AttributesDescriptor("Semicolon", UISyntaxHighlighter.SEMICOLON),
            new AttributesDescriptor("Comma", UISyntaxHighlighter.COMMA),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return null;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new UISyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return """
                // UI Definition Example
                Group {
                  Anchor: (Width: 400, Height: 250);
                  Background: #1a1a2e;
                  LayoutMode: Top;
                  Padding: (Full: 20);
                
                  Label #Title {
                    Text: "Hello World!";
                    Anchor: (Height: 40);
                    Style: (FontSize: 24, TextColor: #ffffff, Alignment: Center);
                  }
                  
                  Button #SubmitButton {
                    Text: "Click Me";
                    Anchor: (Width: 150, Height: 35);
                    Background: #0066cc;
                  }
                }
                """;
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "UI File";
    }
}
