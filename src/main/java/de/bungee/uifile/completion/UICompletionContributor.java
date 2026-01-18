package de.bungee.uifile.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import de.bungee.uifile.UILanguage;
import de.bungee.uifile.completion.UITypeDefinitions.PropertyInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Provides code completion for UI files
 */
public class UICompletionContributor extends CompletionContributor {

    public UICompletionContributor() {
        // Completion for all contexts in UI files
        extend(CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(
                PlatformPatterns.psiFile().withLanguage(UILanguage.INSTANCE)
            ),
            new CompletionProvider<>() {
                @Override
                protected void addCompletions(@NotNull CompletionParameters parameters,
                    @NotNull ProcessingContext context,
                    @NotNull CompletionResultSet result) {
                    PsiElement position = parameters.getPosition();
                    String text = position.getText();

                    // Check if we're at the start of a line or after whitespace (likely a component type)
                    PsiElement prev = PsiTreeUtil.prevLeaf(position);
                    String prevText = prev != null ? prev.getText().trim() : "";

                    boolean isAfterBrace = prevText.equals("{") || prevText.isEmpty() || prevText.endsWith(";");
                    boolean isLineStart = prev == null || prevText.isEmpty() ||
                                          prevText.equals("\n") || prevText.equals("\r\n");

                    // Get context - are we inside a component block?
                    String componentType = findParentComponentType(position);

                    if (componentType != null) {
                        // We're inside a component - suggest properties
                        addPropertyCompletions(result, componentType);
                    }

                    // Always suggest UI component types
                    addTypeCompletions(result);
                }
            }
        );
    }

    /**
     * Find the parent component type by searching backwards through the text
     */
    private String findParentComponentType(PsiElement element) {
        PsiElement current = element;
        int braceDepth = 0;

        while (current != null) {
            String text = current.getText();

            // Track brace depth
            if (text.equals("}")) {
                braceDepth++;
            } else if (text.equals("{")) {
                braceDepth--;

                // Found opening brace, now look for the type name before it
                if (braceDepth < 0) {
                    PsiElement prev = PsiTreeUtil.prevLeaf(current);
                    while (prev != null) {
                        String prevText = prev.getText().trim();
                        if (!prevText.isEmpty() && !prevText.equals("\n") && !prevText.equals("\r\n")) {
                            // Check if this is a known UI type
                            if (UITypeDefinitions.isValidType(prevText)) {
                                return prevText;
                            }
                            break;
                        }
                        prev = PsiTreeUtil.prevLeaf(prev);
                    }
                }
            }

            current = PsiTreeUtil.prevLeaf(current);
        }

        return null;
    }

    /**
     * Add UI component type completions
     */
    private void addTypeCompletions(@NotNull CompletionResultSet result) {
        for (String type : UITypeDefinitions.getUITypes()) {
            result.addElement(
                LookupElementBuilder.create(type + " {\n    \n}")
                    .withPresentableText(type)
                    .withTypeText("UI Component")
                    .withInsertHandler((insertContext, item) -> {
                        // Move the cursor inside the braces
                        int offset = insertContext.getEditor().getCaretModel().getOffset();
                        insertContext.getEditor().getCaretModel().moveToOffset(offset - 2);
                    })
                    .bold()
            );
        }
    }

    /**
     * Add property completions for a specific component type
     */
    private void addPropertyCompletions(@NotNull CompletionResultSet result, String componentType) {
        List<PropertyInfo> properties = UITypeDefinitions.getPropertiesForType(componentType);

        for (PropertyInfo prop : properties) {
            String insertText = getInsertTextForProperty(prop);

            result.addElement(
                LookupElementBuilder.create(insertText)
                    .withPresentableText(prop.name())
                    .withTypeText(prop.valueType())
                    .withTailText(": " + prop.description(), true)
                    .withInsertHandler((insertContext, item) -> {
                        // Move the cursor to the value position
                        int offset = insertContext.getEditor().getCaretModel().getOffset();
                        String text = insertContext.getDocument().getText();

                        // Find the position after the colon and space
                        int colonPos = text.indexOf(':', Math.max(0, offset - insertText.length()));
                        if (colonPos != -1) {
                            insertContext.getEditor().getCaretModel().moveToOffset(colonPos + 2);
                        }
                    })
            );
        }
    }

    /**
     * Generate appropriate insert text based on a property type
     */
    private String getInsertTextForProperty(PropertyInfo prop) {
        return switch (prop.valueType()) {
            case "color" -> prop.name() + ": #";
            case "string" -> prop.name() + ": \"\";";
            case "number" -> prop.name() + ": 0;";
            case "boolean" -> prop.name() + ": true;";
            case "style block" -> prop.name() + ": ();";
            case "padding value", "margin value" -> prop.name() + ": (Full: 0);";
            case "anchor value" -> prop.name() + ": (Width: 0, Height: 0);";
            default -> prop.name() + ": ;";
        };
    }
}

