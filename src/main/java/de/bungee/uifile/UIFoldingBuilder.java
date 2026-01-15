package de.bungee.uifile;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import de.bungee.uifile.lexer.UILexer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Provides code folding support for UI files. Allows folding of nested {} blocks (components and properties).
 */
public class UIFoldingBuilder extends FoldingBuilderEx {

    @NotNull
    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document,
        boolean quick) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        collectFoldingRegions(root.getNode(), descriptors, document);
        return descriptors.toArray(new FoldingDescriptor[0]);
    }

    /**
     * Recursively collects all foldable regions (blocks with {})
     */
    private void collectFoldingRegions(@NotNull ASTNode node, List<FoldingDescriptor> descriptors, Document document) {
        Deque<ASTNode> braceStack = new ArrayDeque<>();
        processNode(node, braceStack, descriptors, document);
    }

    /**
     * Process a node and its children to find brace pairs
     */
    private void processNode(@NotNull ASTNode node, Deque<ASTNode> braceStack,
        List<FoldingDescriptor> descriptors, Document document) {
        IElementType elementType = node.getElementType();

        if (elementType == UILexer.LBRACE) {
            braceStack.push(node);
        } else if (elementType == UILexer.RBRACE && !braceStack.isEmpty()) {
            ASTNode lbraceNode = braceStack.pop();

            int startOffset = lbraceNode.getStartOffset();
            int endOffset = node.getStartOffset() + node.getTextLength();

            int startLine = document.getLineNumber(startOffset);
            int endLine = document.getLineNumber(endOffset);

            if (endLine > startLine) {
                ASTNode parent = lbraceNode.getTreeParent();
                if (parent != null) {
                    TextRange range = new TextRange(startOffset, endOffset);
                    String placeholder = getPlaceholderTextForBrace(lbraceNode);
                    descriptors.add(new FoldingDescriptor(parent, range, null, placeholder));
                }
            }
        }

        for (ASTNode child = node.getFirstChildNode(); child != null; child = child.getTreeNext()) {
            processNode(child, braceStack, descriptors, document);
        }
    }

    private String getPlaceholderTextForBrace(@NotNull ASTNode lbraceNode) {
        ASTNode prev = lbraceNode.getTreePrev();
        while (prev != null && (prev.getElementType() == UILexer.WHITE_SPACE ||
                                prev.getElementType() == UILexer.COMMENT)) {
            prev = prev.getTreePrev();
        }

        if (prev != null) {
            String text = prev.getText();
            if (!text.isEmpty()) {
                return text + " {...}";
            }
        }
        return "{...}";
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {

        return "{...}";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}
