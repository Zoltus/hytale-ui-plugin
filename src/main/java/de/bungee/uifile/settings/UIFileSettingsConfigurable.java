package de.bungee.uifile.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class UIFileSettingsConfigurable implements Configurable {
    private UIFileSettingsComponent mySettingsComponent;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Hytale UI File";
    }

    @Override
    public @Nullable JComponent createComponent() {
        mySettingsComponent = new UIFileSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        UIFileSettings settings = UIFileSettings.getInstance();
        return mySettingsComponent.isNewFileActionEnabled() != settings.isNewFileActionEnabled();
    }

    @Override
    public void apply() {
        UIFileSettings settings = UIFileSettings.getInstance();
        settings.setNewFileActionEnabled(mySettingsComponent.isNewFileActionEnabled());
    }

    @Override
    public void reset() {
        UIFileSettings settings = UIFileSettings.getInstance();
        mySettingsComponent.setNewFileActionEnabled(settings.isNewFileActionEnabled());
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}

