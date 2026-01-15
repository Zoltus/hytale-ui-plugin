package de.bungee.uifile;

import com.intellij.lang.Language;

public class UILanguage extends Language {
    public static final UILanguage INSTANCE = new UILanguage();

    private UILanguage() {
        super("UI");
    }
}
