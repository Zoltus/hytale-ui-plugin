package de.bungee.uifile.lexer;

import com.intellij.psi.tree.IElementType;
import de.bungee.uifile.UILanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class UITokenType extends IElementType {
    public UITokenType(@NotNull @NonNls String debugName) {
        super(debugName, UILanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "UITokenType." + super.toString();
    }
}
