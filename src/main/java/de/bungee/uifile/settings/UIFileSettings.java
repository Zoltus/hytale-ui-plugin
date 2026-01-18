package de.bungee.uifile.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "de.bungee.uifile.settings.UIFileSettings",
    storages = @Storage("UIFilePlugin.xml")
)
public class UIFileSettings implements PersistentStateComponent<UIFileSettings.State> {

    private State myState = new State();

    public static UIFileSettings getInstance() {
        return ApplicationManager.getApplication().getService(UIFileSettings.class);
    }

    @Nullable
    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

    public boolean isNewFileActionEnabled() {
        return myState.enableNewFileAction;
    }

    public void setNewFileActionEnabled(boolean enabled) {
        myState.enableNewFileAction = enabled;
    }

    public static class State {
        public boolean enableNewFileAction = true; // Standard: aktiviert
    }
}

