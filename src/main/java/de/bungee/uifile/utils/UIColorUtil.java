package de.bungee.uifile.utils;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Utility class for color handling in UI files.
 */
public final class UIColorUtil {

    private UIColorUtil() {
        // Utility class
    }

    /**
     * Converts a Color to its hex string representation. Returns #rrggbb format for opaque colors, #rrggbbaa for colors
     * with transparency.
     *
     * @param color the color to convert
     * @return hex string representation
     */
    @NotNull
    public static String colorToHex(@NotNull Color color) {
        if (color.getAlpha() == 255) {
            return String.format("#%02x%02x%02x",
                color.getRed(), color.getGreen(), color.getBlue());
        }
        return String.format("#%02x%02x%02x%02x",
            color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    /**
     * Parses a hex color string to a Color object. Supports formats: #rgb, #rrggbb, #rrggbbaa
     *
     * @param hexColor the hex color string
     * @return the parsed Color, or null if parsing fails
     */
    @Nullable
    public static Color parseHexColor(@Nullable String hexColor) {
        if (hexColor == null || !hexColor.startsWith("#")) {
            return null;
        }

        String hex = hexColor.substring(1);
        try {
            int r, g, b, a = 255;

            switch (hex.length()) {
                case 3:
                    r = Integer.parseInt(hex.substring(0, 1), 16) * 17;
                    g = Integer.parseInt(hex.substring(1, 2), 16) * 17;
                    b = Integer.parseInt(hex.substring(2, 3), 16) * 17;
                    break;
                case 6:
                    r = Integer.parseInt(hex.substring(0, 2), 16);
                    g = Integer.parseInt(hex.substring(2, 4), 16);
                    b = Integer.parseInt(hex.substring(4, 6), 16);
                    break;
                case 8:
                    r = Integer.parseInt(hex.substring(0, 2), 16);
                    g = Integer.parseInt(hex.substring(2, 4), 16);
                    b = Integer.parseInt(hex.substring(4, 6), 16);
                    a = Integer.parseInt(hex.substring(6, 8), 16);
                    break;
                default:
                    return null;
            }

            return new JBColor(new Color(r, g, b, a), new Color(r, g, b, a));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Updates the color value in the PSI element's document.
     *
     * @param project  the project context
     * @param element  the PSI element to replace
     * @param newColor the new color value
     */
    public static void updateColorInCode(@NotNull Project project,
        @NotNull PsiElement element,
        @NotNull Color newColor) {
        WriteCommandAction.runWriteCommandAction(project, "Change Color", null, () -> {
            if (!element.isValid()) {
                return;
            }

            PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
            Document document = psiDocumentManager.getDocument(element.getContainingFile());

            if (document == null) {
                return;
            }

            String newColorString = colorToHex(newColor);
            document.replaceString(
                element.getTextRange().getStartOffset(),
                element.getTextRange().getEndOffset(),
                newColorString
            );
            psiDocumentManager.commitDocument(document);
        });
    }
}

