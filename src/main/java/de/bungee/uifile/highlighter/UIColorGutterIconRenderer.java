package de.bungee.uifile.highlighter;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
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
    private final PsiElement element;

    public UIColorGutterIconRenderer(@NotNull Color color, @NotNull String colorString, @NotNull PsiElement element) {
        this.color = color;
        this.colorString = colorString;
        this.icon = new ColorIcon(ICON_SIZE, color);
        this.element = element;
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
            return String.format("%s (R:%d, G:%d, B:%d) - Click to change color",
                colorString, color.getRed(), color.getGreen(), color.getBlue());
        }
        return String.format("%s (R:%d, G:%d, B:%d, A:%d) - Click to change color",
            colorString, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    @Nullable
    @Override
    public AnAction getClickAction() {
        return new UIColorPickerAction(element, color);
    }

    @Override
    public boolean isNavigateAction() {
        return true;
    }

    @NotNull
    @Override
    public Alignment getAlignment() {
        return Alignment.LEFT;
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

