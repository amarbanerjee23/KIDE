package com.mncml.dsl.ui.syntaxcoloring;

import com.mncml.dsl.ide.highlighting.MncSemanticRegionProvider;
import com.mncml.dsl.ide.highlighting.MncSemanticRegionProvider.SemanticRegion;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;

@SuppressWarnings("all")
public class MncSemanticHighlightingCalculator implements ISemanticHighlightingCalculator {
  private final MncSemanticRegionProvider regionProvider = new MncSemanticRegionProvider();

  @Override
  public void provideHighlightingFor(
      final XtextResource resource,
      final IHighlightedPositionAcceptor acceptor) {
    for (SemanticRegion region : regionProvider.getSemanticRegions(resource)) {
      acceptor.addPosition(region.getOffset(), region.getLength(), styleId(region.getKind()));
    }
  }

  private String styleId(final String kind) {
    if (MncSemanticRegionProvider.COMMAND_REFERENCE.equals(kind)) {
      return MncHighlightingConfiguration.CRB_COMMAND;
    }
    if (MncSemanticRegionProvider.EVENT_REFERENCE.equals(kind)) {
      return MncHighlightingConfiguration.EB_EVENT;
    }
    if (MncSemanticRegionProvider.ALARM_REFERENCE.equals(kind)) {
      return MncHighlightingConfiguration.AB_ALARM;
    }
    if (MncSemanticRegionProvider.DATA_REFERENCE.equals(kind)) {
      return MncHighlightingConfiguration.DT_DATA;
    }
    if (MncSemanticRegionProvider.STATE_REFERENCE.equals(kind)) {
      return MncHighlightingConfiguration.TR_STATE;
    }
    return kind;
  }
}
