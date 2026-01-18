package de.bungee.uifile.highlighter;

import de.bungee.uifile.lexer.UILexer;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class UISyntaxHighlighter extends SyntaxHighlighterBase {

    // Define color keys for different token types
    public static final TextAttributesKey COMPONENT =
        createTextAttributesKey("UI_COMPONENT", DefaultLanguageHighlighterColors.CLASS_NAME);

    public static final TextAttributesKey PROPERTY =
        createTextAttributesKey("UI_PROPERTY", DefaultLanguageHighlighterColors.INSTANCE_FIELD);

    public static final TextAttributesKey STRING =
        createTextAttributesKey("UI_STRING", DefaultLanguageHighlighterColors.STRING);

    public static final TextAttributesKey NUMBER =
        createTextAttributesKey("UI_NUMBER", DefaultLanguageHighlighterColors.NUMBER);

    public static final TextAttributesKey COLOR =
        createTextAttributesKey("UI_COLOR", DefaultLanguageHighlighterColors.NUMBER);

    public static final TextAttributesKey IDENTIFIER =
        createTextAttributesKey("UI_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);

    public static final TextAttributesKey COMMENT =
        createTextAttributesKey("UI_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    public static final TextAttributesKey BRACES =
        createTextAttributesKey("UI_BRACES", DefaultLanguageHighlighterColors.BRACES);

    public static final TextAttributesKey BRACKETS =
        createTextAttributesKey("UI_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);

    public static final TextAttributesKey PARENTHESES =
        createTextAttributesKey("UI_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);

    public static final TextAttributesKey COLON =
        createTextAttributesKey("UI_COLON", DefaultLanguageHighlighterColors.OPERATION_SIGN);

    public static final TextAttributesKey SEMICOLON =
        createTextAttributesKey("UI_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON);

    public static final TextAttributesKey COMMA =
        createTextAttributesKey("UI_COMMA", DefaultLanguageHighlighterColors.COMMA);

    public static final TextAttributesKey AT =
        createTextAttributesKey("UI_AT", DefaultLanguageHighlighterColors.OPERATION_SIGN);

    public static final TextAttributesKey EQUALS =
        createTextAttributesKey("UI_EQUALS", DefaultLanguageHighlighterColors.OPERATION_SIGN);

    public static final TextAttributesKey DOLLAR =
        createTextAttributesKey("UI_DOLLAR", DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey DOT =
        createTextAttributesKey("UI_DOT", DefaultLanguageHighlighterColors.DOT);

    public static final TextAttributesKey BAD_CHARACTER =
        createTextAttributesKey("UI_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    private static final TextAttributesKey[] COMPONENT_KEYS = new TextAttributesKey[]{COMPONENT};
    private static final TextAttributesKey[] PROPERTY_KEYS = new TextAttributesKey[]{PROPERTY};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] COLOR_KEYS = new TextAttributesKey[]{COLOR};
    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] BRACES_KEYS = new TextAttributesKey[]{BRACES};
    private static final TextAttributesKey[] BRACKETS_KEYS = new TextAttributesKey[]{BRACKETS};
    private static final TextAttributesKey[] PARENTHESES_KEYS = new TextAttributesKey[]{PARENTHESES};
    private static final TextAttributesKey[] COLON_KEYS = new TextAttributesKey[]{COLON};
    private static final TextAttributesKey[] SEMICOLON_KEYS = new TextAttributesKey[]{SEMICOLON};
    private static final TextAttributesKey[] COMMA_KEYS = new TextAttributesKey[]{COMMA};
    private static final TextAttributesKey[] AT_KEYS = new TextAttributesKey[]{AT};
    private static final TextAttributesKey[] EQUALS_KEYS = new TextAttributesKey[]{EQUALS};
    private static final TextAttributesKey[] DOLLAR_KEYS = new TextAttributesKey[]{DOLLAR};
    private static final TextAttributesKey[] DOT_KEYS = new TextAttributesKey[]{DOT};
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new UILexer();
    }

    @NotNull
    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(UILexer.COMPONENT)) {
            return COMPONENT_KEYS;
        } else if (tokenType.equals(UILexer.PROPERTY)) {
            return PROPERTY_KEYS;
        } else if (tokenType.equals(UILexer.STRING)) {
            return STRING_KEYS;
        } else if (tokenType.equals(UILexer.NUMBER)) {
            return NUMBER_KEYS;
        } else if (tokenType.equals(UILexer.COLOR)) {
            return COLOR_KEYS;
        } else if (tokenType.equals(UILexer.IDENTIFIER)) {
            return IDENTIFIER_KEYS;
        } else if (tokenType.equals(UILexer.COMMENT)) {
            return COMMENT_KEYS;
        } else if (tokenType.equals(UILexer.LBRACE) || tokenType.equals(UILexer.RBRACE)) {
            return BRACES_KEYS;
        } else if (tokenType.equals(UILexer.LPAREN) || tokenType.equals(UILexer.RPAREN)) {
            return PARENTHESES_KEYS;
        } else if (tokenType.equals(UILexer.COLON)) {
            return COLON_KEYS;
        } else if (tokenType.equals(UILexer.SEMICOLON)) {
            return SEMICOLON_KEYS;
        } else if (tokenType.equals(UILexer.COMMA)) {
            return COMMA_KEYS;
        } else if (tokenType.equals(UILexer.AT)) {
            return AT_KEYS;
        } else if (tokenType.equals(UILexer.EQUALS)) {
            return EQUALS_KEYS;
        } else if (tokenType.equals(UILexer.DOLLAR)) {
            return DOLLAR_KEYS;
        } else if (tokenType.equals(UILexer.DOT)) {
            return DOT_KEYS;
        } else if (tokenType.equals(UILexer.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
