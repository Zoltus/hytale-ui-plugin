package de.bungee.uifile.parser;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class UIElement extends ASTWrapperPsiElement {
    public UIElement(@NotNull ASTNode node) {
        super(node);
    }
}
