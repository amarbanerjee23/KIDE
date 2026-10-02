package com.kide.languageserver;

import org.eclipse.lsp4j.SemanticTokenTypes;
import org.eclipse.xtext.ide.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;

import com.capability.ide.highlighting.CapabilitySemanticRegionProvider;
import com.mncml.dsl.ide.highlighting.MncSemanticRegionProvider;
import com.smr.activity.dsl.ide.highlighting.ActivityDiagramSemanticRegionProvider;

/**
 * LSP semantic-token adapter over the same platform-neutral semantic regions
 * consumed by the Eclipse editors.
 */
final class KideSemanticHighlightingCalculator
        implements ISemanticHighlightingCalculator {

    private final String extension;
    private final CapabilitySemanticRegionProvider capability =
            new CapabilitySemanticRegionProvider();
    private final MncSemanticRegionProvider mnc =
            new MncSemanticRegionProvider();
    private final ActivityDiagramSemanticRegionProvider activity =
            new ActivityDiagramSemanticRegionProvider();

    KideSemanticHighlightingCalculator(String extension) {
        this.extension = extension;
    }

    @Override
    public void provideHighlightingFor(
            XtextResource resource,
            IHighlightedPositionAcceptor acceptor,
            CancelIndicator cancelIndicator) {
        if (cancelIndicator != null && cancelIndicator.isCanceled()) {
            return;
        }

        switch (extension) {
        case "cap":
            capability.getSemanticRegions(resource).forEach(region ->
                    acceptor.addPosition(
                            region.getOffset(),
                            region.getLength(),
                            capabilityToken(region.getKind())));
            return;
        case "mncspec":
            mnc.getSemanticRegions(resource).forEach(region ->
                    acceptor.addPosition(
                            region.getOffset(),
                            region.getLength(),
                            mncToken(region.getKind())));
            return;
        case "activity":
            activity.getSemanticRegions(resource).forEach(region ->
                    acceptor.addPosition(
                            region.getOffset(),
                            region.getLength(),
                            activityToken(region.getKind())));
            return;
        default:
            // DML, Operation and KRL have no custom Eclipse semantic
            // highlighting. Their lexical grammar remains the presentation
            // baseline and semantic-token output is intentionally empty.
        }
    }

    private static String capabilityToken(String kind) {
        if (CapabilitySemanticRegionProvider.CAPABILITY_NAME.equals(kind)) {
            return SemanticTokenTypes.Class;
        }
        if (CapabilitySemanticRegionProvider.INTERFACE_ITEM.equals(kind)) {
            return SemanticTokenTypes.Property;
        }
        if (CapabilitySemanticRegionProvider.STRUCTURAL_KEYWORD.equals(kind)) {
            return SemanticTokenTypes.Keyword;
        }
        return SemanticTokenTypes.Property;
    }

    private static String activityToken(String kind) {
        if (ActivityDiagramSemanticRegionProvider.ACTIVITY_NAME.equals(kind)) {
            return SemanticTokenTypes.Function;
        }
        if (ActivityDiagramSemanticRegionProvider.CAPABILITY_REFERENCE.equals(kind)) {
            return SemanticTokenTypes.Type;
        }
        if (ActivityDiagramSemanticRegionProvider.DATA_REFERENCE.equals(kind)) {
            return SemanticTokenTypes.Variable;
        }
        return SemanticTokenTypes.Variable;
    }

    private static String mncToken(String kind) {
        if (MncSemanticRegionProvider.COMMAND_REFERENCE.equals(kind)) {
            return SemanticTokenTypes.Method;
        }
        if (MncSemanticRegionProvider.EVENT_REFERENCE.equals(kind)
                || MncSemanticRegionProvider.ALARM_REFERENCE.equals(kind)) {
            return SemanticTokenTypes.Event;
        }
        if (MncSemanticRegionProvider.DATA_REFERENCE.equals(kind)) {
            return SemanticTokenTypes.Property;
        }
        if (MncSemanticRegionProvider.STATE_REFERENCE.equals(kind)) {
            return SemanticTokenTypes.EnumMember;
        }
        return SemanticTokenTypes.Property;
    }
}
