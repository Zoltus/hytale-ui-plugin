package de.bungee.uifile.settings;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.FormBuilder;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class UIFileSettingsComponent {
    private final JPanel myMainPanel;
    private final JBCheckBox myEnableNewFileAction =
        new JBCheckBox("Enable 'New UI File' action in context menu");

    public UIFileSettingsComponent() {
        myMainPanel = FormBuilder.createFormBuilder()
            .addComponent(myEnableNewFileAction, 1)
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return myEnableNewFileAction;
    }

    public boolean isNewFileActionEnabled() {
        return myEnableNewFileAction.isSelected();
    }

    public void setNewFileActionEnabled(boolean enabled) {
        myEnableNewFileAction.setSelected(enabled);
    }
}

