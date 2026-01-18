package de.bungee.uifile.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import de.bungee.uifile.UILanguage;
import de.bungee.uifile.lexer.UILexer;
import de.bungee.uifile.psi.UIElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UIFormattingModelBuilder implements FormattingModelBuilder {
    @Override
    public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext) {
        CodeStyleSettings settings = formattingContext.getCodeStyleSettings();
        SpacingBuilder spacingBuilder = createSpacingBuilder(settings);
        
        return FormattingModelProvider.createFormattingModelForPsiFile(
                formattingContext.getContainingFile(),
                new UIBlock(formattingContext.getNode(), Wrap.createWrap(WrapType.NONE, false),
                        null, spacingBuilder),
                settings);
    }

    private static SpacingBuilder createSpacingBuilder(CodeStyleSettings settings) {
        return new SpacingBuilder(settings, UILanguage.INSTANCE)
                .around(UILexer.EQUALS).spaceIf(true)
                .after(UILexer.COLON).spaceIf(true)
                .after(UILexer.COMMA).spaceIf(true)
                .before(UILexer.SEMICOLON).spaceIf(false)
                .after(UILexer.AT).spaceIf(false)
                .around(UILexer.DOT).spaceIf(false)
                .before(UIElementType.BLOCK).spaceIf(true)
                .between(UILexer.IDENTIFIER, UIElementType.PAREN_BLOCK).spaceIf(false)
                .between(UILexer.COMPONENT, UIElementType.PAREN_BLOCK).spaceIf(false)
                .between(UILexer.COLOR, UIElementType.PAREN_BLOCK).spaceIf(false)
                .between(UILexer.COLON, UIElementType.PAREN_BLOCK).spaceIf(true)
                .between(UILexer.EQUALS, UIElementType.PAREN_BLOCK).spaceIf(true);
    }

    @Nullable
    @Override
    public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
        return null;
    }
}
