package de.bungee.uifile.psi;

import de.bungee.uifile.UIFileType;
import de.bungee.uifile.UILanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class UIFile extends PsiFileBase {
    public UIFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, UILanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return UIFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "UI File";
    }
}
