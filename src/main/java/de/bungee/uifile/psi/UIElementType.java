package de.bungee.uifile.psi;

import com.intellij.psi.tree.IElementType;
import de.bungee.uifile.UILanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class UIElementType extends IElementType {
    public UIElementType(@NotNull @NonNls String debugName) {
        super(debugName, UILanguage.INSTANCE);
    }
}
