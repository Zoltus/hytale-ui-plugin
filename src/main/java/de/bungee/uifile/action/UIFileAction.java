package de.bungee.uifile.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import de.bungee.uifile.UIFileIcons;
import de.bungee.uifile.settings.UIFileSettings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ResourceBundle;

public class UIFileAction extends AnAction {
    private static final String UI_FILE_EXTENSION = ".ui";
    private static final String DIALOG_TITLE = "New UI File";
    private static final String DIALOG_MESSAGE = "Enter UI file name:";
    private static final ResourceBundle BUNDLE =
        ResourceBundle.getBundle("messages.UIFileBundle");

    public UIFileAction() {
        super();
        getTemplatePresentation().setText(BUNDLE.getString("action.UIFileAction.text"));
        getTemplatePresentation().setDescription(BUNDLE.getString("action.UIFileAction.description"));
        getTemplatePresentation().setIcon(UIFileIcons.FILE);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // Prüfe, ob die Action in den Settings aktiviert ist
        UIFileSettings settings = UIFileSettings.getInstance();
        if (settings == null || !settings.isNewFileActionEnabled()) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }

        // Action ist aktiviert - zeige sie an
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        // Versuche das ausgewählte Verzeichnis zu bekommen
        VirtualFile directory = e.getData(CommonDataKeys.VIRTUAL_FILE);

        // Falls eine Datei ausgewählt ist, nimm das Parent-Verzeichnis
        if (directory != null && !directory.isDirectory()) {
            directory = directory.getParent();
        }

        // Falls kein Verzeichnis ausgewählt ist, nimm das Projekt-Basisverzeichnis
        if (directory == null) {
            directory = project.getWorkspaceFile();
            if (directory != null) {
                directory = directory.getParent();
            }
        }

        if (directory == null || !directory.isDirectory()) {
            Messages.showErrorDialog(project, "Could not determine target directory", "Error");
            return;
        }

        String fileName = promptForFileName(project);
        if (!isValidFileName(fileName)) {
            return;
        }

        String normalizedFileName = normalizeFileName(fileName);
        createUIFile(project, directory, normalizedFileName);
    }


    private String promptForFileName(Project project) {
        return Messages.showInputDialog(
            project,
            DIALOG_MESSAGE,
            DIALOG_TITLE,
            Messages.getQuestionIcon()
        );
    }

    private boolean isValidFileName(String fileName) {
        return fileName != null && !fileName.isBlank();
    }

    private String normalizeFileName(String fileName) {
        return fileName.endsWith(UI_FILE_EXTENSION) ? fileName : fileName + UI_FILE_EXTENSION;
    }

    private void createUIFile(Project project, VirtualFile directory, String fileName) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            try {
                if (directory.findChild(fileName) == null) {
                    directory.createChildData(this, fileName);
                }
            } catch (IOException ex) {
                Logger.getInstance(UIFileAction.class)
                    .error("Failed to create UI file: " + fileName, ex);
                throw new RuntimeException("Failed to create UI file: " + fileName, ex);
            }
        });
    }
}
