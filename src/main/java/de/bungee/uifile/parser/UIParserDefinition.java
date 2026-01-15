package de.bungee.uifile.parser;

import de.bungee.uifile.UILanguage;
import de.bungee.uifile.lexer.UILexer;
import de.bungee.uifile.psi.UIFile;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

public class UIParserDefinition implements ParserDefinition {
    public static final IFileElementType FILE = new IFileElementType(UILanguage.INSTANCE);

    public static final TokenSet WHITE_SPACES = TokenSet.create(UILexer.WHITE_SPACE);
    public static final TokenSet COMMENTS = TokenSet.create(UILexer.COMMENT);
    public static final TokenSet STRINGS = TokenSet.create(UILexer.STRING);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new UILexer();
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new UIParser();
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return STRINGS;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        return new UIElement(node);
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new UIFile(viewProvider);
    }
}
