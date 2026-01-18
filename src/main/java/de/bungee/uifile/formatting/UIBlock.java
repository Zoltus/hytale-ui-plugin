package de.bungee.uifile.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.formatter.common.AbstractBlock;
import de.bungee.uifile.lexer.UILexer;
import de.bungee.uifile.psi.UIElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UIBlock extends AbstractBlock {
    private final SpacingBuilder spacingBuilder;

    protected UIBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment,
                     SpacingBuilder spacingBuilder) {
        super(node, wrap, alignment);
        this.spacingBuilder = spacingBuilder;
    }

    @Override
    protected List<Block> buildChildren() {
        List<Block> blocks = new ArrayList<>();
        ASTNode child = myNode.getFirstChildNode();
        while (child != null) {
            IElementType type = child.getElementType();
            if (type != TokenType.WHITE_SPACE && type != UILexer.WHITE_SPACE && child.getTextLength() > 0) {
                Block block = new UIBlock(child, Wrap.createWrap(WrapType.NONE, false),
                        null, spacingBuilder);
                blocks.add(block);
            }
            child = child.getTreeNext();
        }
        return blocks;
    }

    @Override
    public Indent getIndent() {
        IElementType type = myNode.getElementType();
        if (type == UIElementType.BLOCK || type == UIElementType.PAREN_BLOCK) {
            return Indent.getNoneIndent();
        }
        
        ASTNode parent = myNode.getTreeParent();
        if (parent != null) {
            IElementType parentType = parent.getElementType();
            if (parentType == UIElementType.BLOCK || parentType == UIElementType.PAREN_BLOCK) {
                if (type != UILexer.LBRACE && type != UILexer.RBRACE &&
                    type != UILexer.LPAREN && type != UILexer.RPAREN) {
                    return Indent.getNormalIndent();
                }
            }
        }
        return Indent.getNoneIndent();
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return spacingBuilder.getSpacing(this, child1, child2);
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }

    @Override
    public @Nullable ChildAttributes getChildAttributes(int newChildIndex) {
        if (myNode.getElementType() == UIElementType.BLOCK || myNode.getElementType() == UIElementType.PAREN_BLOCK) {
            return new ChildAttributes(Indent.getNormalIndent(), null);
        }
        return new ChildAttributes(Indent.getNoneIndent(), null);
    }
}
