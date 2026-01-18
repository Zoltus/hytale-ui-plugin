package de.bungee.uifile.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import de.bungee.uifile.lexer.UILexer;
import de.bungee.uifile.psi.UIElementType;

public class UIParser implements PsiParser {
    @NotNull
    @Override
    public ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder builder) {
        final PsiBuilder.Marker rootMarker = builder.mark();
        
        while (!builder.eof()) {
            parseElement(builder);
        }
        
        rootMarker.done(root);
        return builder.getTreeBuilt();
    }

    private void parseElement(PsiBuilder builder) {
        IElementType type = builder.getTokenType();
        if (type == UILexer.LBRACE) {
            parseBlock(builder, UILexer.LBRACE, UILexer.RBRACE, UIElementType.BLOCK);
        } else if (type == UILexer.LPAREN) {
            parseBlock(builder, UILexer.LPAREN, UILexer.RPAREN, UIElementType.PAREN_BLOCK);
        } else if (type == UILexer.COMPONENT || type == UILexer.PROPERTY || type == UILexer.IDENTIFIER || type == UILexer.AT || type == UILexer.DOLLAR) {
            PsiBuilder.Marker marker = builder.mark();
            builder.advanceLexer();
            while (!builder.eof()) {
                IElementType nextType = builder.getTokenType();
                if (nextType == UILexer.LBRACE || nextType == UILexer.LPAREN) {
                    parseElement(builder);
                    break;
                }
                if (nextType == UILexer.SEMICOLON || nextType == UILexer.RBRACE || 
                    nextType == UILexer.RPAREN || nextType == UILexer.COMMA) {
                    break;
                }
                // Handle complex property values like $C.@TextField
                if (nextType == UILexer.COMPONENT || nextType == UILexer.PROPERTY || 
                    nextType == UILexer.IDENTIFIER || nextType == UILexer.AT || 
                    nextType == UILexer.DOLLAR || nextType == UILexer.DOT || 
                    nextType == UILexer.HASH || nextType == UILexer.EQUALS ||
                    nextType == UILexer.COLON) {
                    builder.advanceLexer();
                } else {
                    break;
                }
            }
            marker.done(UIElementType.UI_ELEMENT);
        } else {
            builder.advanceLexer();
        }
    }

    private void parseBlock(PsiBuilder builder, IElementType left, IElementType right, IElementType blockType) {
        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer(); // left brace/paren
        
        while (!builder.eof() && builder.getTokenType() != right) {
            parseElement(builder);
        }
        
        if (builder.getTokenType() == right) {
            builder.advanceLexer();
        }
        
        marker.done(blockType);
    }
}
