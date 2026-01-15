package de.bungee.uifile;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class UIFileType extends LanguageFileType {
    public static final UIFileType INSTANCE = new UIFileType();

    private UIFileType() {
        super(UILanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "UI File";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "UI definition file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "ui";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        // Try to load an icon if available
        try {
            return IconLoader.getIcon("/icons/ui-file.svg", UIFileType.class);
        } catch (Exception e) {
            // If no icon is available, return null (IntelliJ will use default icon)
            return null;
        }
    }
}
