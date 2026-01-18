package de.bungee.uifile.highlighter;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.ColorPicker;
import de.bungee.uifile.utils.UIColorUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Action to open a color picker for UI color values.
 */
public class UIColorPickerAction extends AnAction {
    private final PsiElement element;
    private final Color currentColor;

    public UIColorPickerAction(@NotNull PsiElement element, @NotNull Color currentColor) {
        super("Choose Color");
        this.element = element;
        this.currentColor = currentColor;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    @SuppressWarnings("ConstantConditions") // ColorPicker.showDialog accepts null parent component
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = element.getProject();

        if (!element.isValid()) {
            return;
        }

        // Determine the parent component for the color picker dialog
        Component parentComponent = null;
        if (e.getInputEvent() != null) {
            parentComponent = e.getInputEvent().getComponent();
        }

        // Open the color picker (null parent is acceptable)
        Color newColor = ColorPicker.showDialog(
            parentComponent,
            "Choose Color",
            currentColor,
            true, // enableOpacity
            null,
            true
        );

        if (newColor != null && !newColor.equals(currentColor)) {
            UIColorUtil.updateColorInCode(project, element, newColor);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(element.isValid());
    }
}
