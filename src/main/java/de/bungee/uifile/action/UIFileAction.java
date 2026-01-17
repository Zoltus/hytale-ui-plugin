package de.bungee.uifile.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class UIFileAction extends AnAction {
    private static final String UI_FILE_EXTENSION = ".ui";
    private static final String DIALOG_TITLE = "New UI File";
    private static final String DIALOG_MESSAGE = "Enter UI file name:";

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VirtualFile directory = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (!isValidDirectory(directory)) return;

        String fileName = promptForFileName(e.getProject());
        if (!isValidFileName(fileName)) return;

        String normalizedFileName = normalizeFileName(fileName);
        createUIFile(e.getProject(), directory, normalizedFileName);
    }

    private boolean isValidDirectory(VirtualFile directory) {
        return directory != null && directory.isDirectory();
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
