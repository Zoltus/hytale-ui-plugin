package de.bungee.uifile.highlighter;

import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.util.ui.ColorIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class UIColorGutterIconRenderer extends GutterIconRenderer {
    private static final int ICON_SIZE = 12;

    private final Color color;
    private final String colorString;
    private final Icon icon;

    public UIColorGutterIconRenderer(@NotNull Color color, @NotNull String colorString) {
        this.color = color;
        this.colorString = colorString;
        this.icon = new ColorIcon(ICON_SIZE, color);
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return icon;
    }

    @Nullable
    @Override
    public String getTooltipText() {
        if (color.getAlpha() == 255) {
            return String.format("%s (R:%d, G:%d, B:%d)",
                colorString, color.getRed(), color.getGreen(), color.getBlue());
        }
        return String.format("%s (R:%d, G:%d, B:%d, A:%d)",
            colorString, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UIColorGutterIconRenderer that = (UIColorGutterIconRenderer) obj;
        return color.equals(that.color) && colorString.equals(that.colorString);
    }

    @Override
    public int hashCode() {
        return 31 * color.hashCode() + colorString.hashCode();
    }
}

