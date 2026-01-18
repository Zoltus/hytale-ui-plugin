package de.bungee.uifile.psi;

import com.intellij.psi.tree.IElementType;
import de.bungee.uifile.UILanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class UIElementType extends IElementType {
    public UIElementType(@NotNull @NonNls String debugName) {
        super(debugName, UILanguage.INSTANCE);
    }

    public static final IElementType BLOCK = new UIElementType("BLOCK");
    public static final IElementType PAREN_BLOCK = new UIElementType("PAREN_BLOCK");
    public static final IElementType UI_ELEMENT = new UIElementType("UI_ELEMENT");
}
