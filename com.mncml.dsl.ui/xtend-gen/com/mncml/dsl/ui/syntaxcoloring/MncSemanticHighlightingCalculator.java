package com.mncml.dsl.ui.syntaxcoloring;

import com.google.common.base.Objects;
import com.google.common.collect.Iterators;
import com.mncml.dsl.ui.syntaxcoloring.MncHighlightingConfiguration;
import java.util.List;
import mncModel.AlarmBlock;
import mncModel.CommandResponseBlock;
import mncModel.DataPointBlock;
import mncModel.EventBlock;
import mncModel.Transition;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.nodemodel.BidiTreeIterable;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultSemanticHighlightingCalculator;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

@SuppressWarnings("all")
public class MncSemanticHighlightingCalculator extends DefaultSemanticHighlightingCalculator implements ISemanticHighlightingCalculator {
  @Override
  public void provideHighlightingFor(final XtextResource resource, final IHighlightedPositionAcceptor acceptor) {
    if ((Objects.equal(resource, null) || Objects.equal(resource.getParseResult(), null))) {
      return;
    }
    List<CommandResponseBlock> listOfCommandResponseBlocks = IteratorExtensions.<CommandResponseBlock>toList(Iterators.<CommandResponseBlock>filter(resource.getAllContents(), CommandResponseBlock.class));
    List<EventBlock> listOfEventBlocks = IteratorExtensions.<EventBlock>toList(Iterators.<EventBlock>filter(resource.getAllContents(), EventBlock.class));
    List<AlarmBlock> listOfAlarmBlocks = IteratorExtensions.<AlarmBlock>toList(Iterators.<AlarmBlock>filter(resource.getAllContents(), AlarmBlock.class));
    List<DataPointBlock> listOfDataBlocks = IteratorExtensions.<DataPointBlock>toList(Iterators.<DataPointBlock>filter(resource.getAllContents(), DataPointBlock.class));
    List<Transition> listOfTramsition = IteratorExtensions.<Transition>toList(Iterators.<Transition>filter(resource.getAllContents(), Transition.class));
    for (final CommandResponseBlock commandResponseBlock : listOfCommandResponseBlocks) {
      {
        ICompositeNode compositeNode = NodeModelUtils.getNode(commandResponseBlock);
        BidiTreeIterable<INode> _asTreeIterable = compositeNode.getAsTreeIterable();
        for (final INode node : _asTreeIterable) {
          if (((node.getGrammarElement() instanceof CrossReference) && 
            (node.getSemanticElement() instanceof CommandResponseBlock))) {
            this.highlightNode(acceptor, node, MncHighlightingConfiguration.CRB_COMMAND);
          }
        }
      }
    }
    for (final EventBlock eventBlock : listOfEventBlocks) {
      {
        ICompositeNode compositeNode = NodeModelUtils.getNode(eventBlock);
        BidiTreeIterable<INode> _asTreeIterable = compositeNode.getAsTreeIterable();
        for (final INode node : _asTreeIterable) {
          if (((node.getGrammarElement() instanceof CrossReference) && 
            (node.getSemanticElement() instanceof EventBlock))) {
            this.highlightNode(acceptor, node, MncHighlightingConfiguration.EB_EVENT);
          }
        }
      }
    }
    for (final AlarmBlock alarmBlock : listOfAlarmBlocks) {
      {
        ICompositeNode compositeNode = NodeModelUtils.getNode(alarmBlock);
        BidiTreeIterable<INode> _asTreeIterable = compositeNode.getAsTreeIterable();
        for (final INode node : _asTreeIterable) {
          if (((node.getGrammarElement() instanceof CrossReference) && 
            (node.getSemanticElement() instanceof AlarmBlock))) {
            this.highlightNode(acceptor, node, MncHighlightingConfiguration.AB_ALARM);
          }
        }
      }
    }
    for (final DataPointBlock dataBlock : listOfDataBlocks) {
      {
        ICompositeNode compositeNode = NodeModelUtils.getNode(dataBlock);
        BidiTreeIterable<INode> _asTreeIterable = compositeNode.getAsTreeIterable();
        for (final INode node : _asTreeIterable) {
          if (((node.getGrammarElement() instanceof CrossReference) && 
            (node.getSemanticElement() instanceof DataPointBlock))) {
            this.highlightNode(acceptor, node, MncHighlightingConfiguration.DT_DATA);
          }
        }
      }
    }
    for (final Transition transition : listOfTramsition) {
      {
        ICompositeNode compositeNode = NodeModelUtils.getNode(transition);
        BidiTreeIterable<INode> _asTreeIterable = compositeNode.getAsTreeIterable();
        for (final INode node : _asTreeIterable) {
          if (((node.getGrammarElement() instanceof CrossReference) && 
            (node.getSemanticElement() instanceof Transition))) {
            this.highlightNode(acceptor, node, MncHighlightingConfiguration.TR_STATE);
          }
        }
      }
    }
  }
}
