package de.bungee.uifile.highlighter;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.ColorPicker;
import com.intellij.ui.JBColor;
import com.intellij.util.IncorrectOperationException;
import de.bungee.uifile.lexer.UILexer;
import de.bungee.uifile.utils.UIColorUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Intention action to open a color picker for UI color values.
 */
public class UIColorChooserIntention extends PsiElementBaseIntentionAction implements IntentionAction {

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element)
        throws IncorrectOperationException {
        if (!element.isValid()) {
            return;
        }

        Color currentColor = UIColorUtil.parseHexColor(element.getText());
        if (currentColor == null) {
            currentColor = JBColor.WHITE;
        }

        Color newColor = ColorPicker.showDialog(
            editor.getComponent(),
            "Choose Color",
            currentColor,
            true,
            null,
            true
        );

        if (newColor != null && !newColor.equals(currentColor)) {
            UIColorUtil.updateColorInCode(project, element, newColor);
        }
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return element.getNode() != null && element.getNode().getElementType() == UILexer.COLOR;
    }

    @NotNull
    @Override
    public String getText() {
        return "Choose color";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "UI file color picker";
    }
}

