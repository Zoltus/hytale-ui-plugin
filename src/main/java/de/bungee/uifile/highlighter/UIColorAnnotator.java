package de.bungee.uifile.highlighter;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.ui.JBColor;
import de.bungee.uifile.lexer.UILexer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class UIColorAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element.getNode().getElementType() != UILexer.COLOR) {
            return;
        }

        String colorString = element.getText();
        Color color = parseHexColor(colorString);
        if (color != null) {
            TextRange range = element.getTextRange();
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(range)
                .gutterIconRenderer(new UIColorGutterIconRenderer(color, colorString, element))
                .create();
        }
    }

    private Color parseHexColor(String hexColor) {
        if (hexColor == null || !hexColor.startsWith("#")) {
            return null;
        }
        String hex = hexColor.substring(1);
        try {
            int r, g, b, a = 255;
            if (hex.length() == 3) {
                r = Integer.parseInt(hex.substring(0, 1), 16) * 17;
                g = Integer.parseInt(hex.substring(1, 2), 16) * 17;
                b = Integer.parseInt(hex.substring(2, 3), 16) * 17;
            } else if (hex.length() == 6) {
                r = Integer.parseInt(hex.substring(0, 2), 16);
                g = Integer.parseInt(hex.substring(2, 4), 16);
                b = Integer.parseInt(hex.substring(4, 6), 16);
            } else if (hex.length() == 8) {
                r = Integer.parseInt(hex.substring(0, 2), 16);
                g = Integer.parseInt(hex.substring(2, 4), 16);
                b = Integer.parseInt(hex.substring(4, 6), 16);
                a = Integer.parseInt(hex.substring(6, 8), 16);
            } else {
                return null;
            }
            return new JBColor(new Color(r, g, b, a), new Color(r, g, b, a));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}




