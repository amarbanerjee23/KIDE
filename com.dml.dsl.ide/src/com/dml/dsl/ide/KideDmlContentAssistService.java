package com.dml.dsl.ide;

import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ide.editor.contentassist.ContentAssistEntry;
import org.eclipse.xtext.ide.editor.contentassist.IIdeContentProposalAcceptor;
import org.eclipse.xtext.ide.server.contentassist.ContentAssistService;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.TextRegion;

import com.dml.dsl.services.DmlGrammarAccess;
import com.google.inject.Inject;

/**
 * KIDE compatibility service for DML content assist on the current Xtext runtime.
 * The legacy generated unordered-group parser can return zero contexts at a legal
 * primitive-entry boundary; this service supplies the same grammar-owned keywords.
 */
public final class KideDmlContentAssistService extends ContentAssistService {
    @Inject
    private DmlGrammarAccess grammarAccess;

    @Override
    protected void createProposals(
            String document,
            TextRegion selection,
            int caretOffset,
            XtextResource resource,
            IIdeContentProposalAcceptor acceptor) {
        super.createProposals(document, selection, caretOffset, resource, acceptor);
        if (!primitiveEntryBoundary(document, caretOffset)) {
            return;
        }
        DmlGrammarAccess.PrimitiveValueTypeElements primitive =
                grammarAccess.getPrimitiveValueTypeAccess();
        addKeyword(primitive.getIntIntKeyword_0_0(), acceptor);
        addKeyword(primitive.getBooleanBooleanKeyword_1_0(), acceptor);
        addKeyword(primitive.getFloatFloatKeyword_2_0(), acceptor);
        addKeyword(primitive.getStringStringKeyword_3_0(), acceptor);
        addKeyword(primitive.getObjectObjectKeyword_4_0(), acceptor);
        addKeyword(primitive.getDateDateKeyword_5_0(), acceptor);
    }

    private static boolean primitiveEntryBoundary(String document, int caretOffset) {
        if (document == null || caretOffset < 0 || caretOffset > document.length()) {
            return false;
        }
        String beforeCaret = document.substring(0, caretOffset);
        int primitives = beforeCaret.lastIndexOf("primitives");
        if (primitives < 0) return false;
        int openBrace = beforeCaret.indexOf('{', primitives + "primitives".length());
        if (openBrace < 0) return false;
        if (beforeCaret.indexOf('}', openBrace + 1) >= 0) return false;
        String tail = beforeCaret.substring(openBrace + 1).stripTrailing();
        return tail.isEmpty() || tail.endsWith(",");
    }

    private static void addKeyword(Keyword keyword, IIdeContentProposalAcceptor acceptor) {
        if (!acceptor.canAcceptMoreProposals()) return;
        ContentAssistEntry entry = new ContentAssistEntry();
        entry.setProposal(keyword.getValue());
        entry.setLabel(keyword.getValue());
        entry.setKind(ContentAssistEntry.KIND_KEYWORD);
        entry.setDescription("DML primitive type");
        acceptor.accept(entry, 0);
    }
}
