package com.dml.dsl.ide;

import java.util.Collection;

import org.eclipse.xtext.ide.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ide.editor.contentassist.IIdeContentProposalAcceptor;
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalProvider;

import com.dml.dsl.services.DmlGrammarAccess;
import com.google.inject.Inject;

/**
 * Compatibility bridge for the legacy DML unordered-group content-assist parser
 * on the current Xtext runtime. Proposals are still sourced from the grammar.
 */
public final class DmlIdeContentProposalProvider extends IdeContentProposalProvider {
    @Inject
    private DmlGrammarAccess grammarAccess;

    @Override
    public void createProposals(
            Collection<ContentAssistContext> contexts,
            IIdeContentProposalAcceptor acceptor) {
        super.createProposals(contexts, acceptor);
        for (ContentAssistContext context : contexts) {
            if (!insideOpenPrimitivesBlock(context)) {
                continue;
            }
            DmlGrammarAccess.PrimitiveValueTypeElements primitive =
                    grammarAccess.getPrimitiveValueTypeAccess();
            createProposals(primitive.getIntIntKeyword_0_0(), context, acceptor);
            createProposals(primitive.getBooleanBooleanKeyword_1_0(), context, acceptor);
            createProposals(primitive.getFloatFloatKeyword_2_0(), context, acceptor);
            createProposals(primitive.getStringStringKeyword_3_0(), context, acceptor);
            createProposals(primitive.getObjectObjectKeyword_4_0(), context, acceptor);
            createProposals(primitive.getDateDateKeyword_5_0(), context, acceptor);
        }
    }

    private static boolean insideOpenPrimitivesBlock(ContentAssistContext context) {
        if (context == null || context.getRootNode() == null) {
            return false;
        }
        String document = context.getRootNode().getText();
        int offset = Math.max(0, Math.min(context.getOffset(), document.length()));
        String beforeCaret = document.substring(0, offset);
        int primitives = beforeCaret.lastIndexOf("primitives");
        if (primitives < 0) {
            return false;
        }
        int openBrace = beforeCaret.indexOf('{', primitives + "primitives".length());
        if (openBrace < 0) {
            return false;
        }
        return beforeCaret.indexOf('}', openBrace + 1) < 0;
    }
}
