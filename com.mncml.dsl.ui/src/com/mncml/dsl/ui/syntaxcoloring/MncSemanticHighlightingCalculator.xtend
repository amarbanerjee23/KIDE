package com.mncml.dsl.ui.syntaxcoloring

import java.util.List
import mncModel.AlarmBlock
import mncModel.CommandResponseBlock
import mncModel.DataPointBlock
import mncModel.EventBlock
import mncModel.Transition
import org.eclipse.xtext.CrossReference
import org.eclipse.xtext.nodemodel.util.NodeModelUtils
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultSemanticHighlightingCalculator
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator

class MncSemanticHighlightingCalculator extends DefaultSemanticHighlightingCalculator implements ISemanticHighlightingCalculator {

	override provideHighlightingFor(XtextResource resource, IHighlightedPositionAcceptor acceptor) {
		if (resource == null || resource.getParseResult() == null)
			return;
		var List<CommandResponseBlock> listOfCommandResponseBlocks = resource.allContents.filter(CommandResponseBlock).
			toList
		var List<EventBlock> listOfEventBlocks = resource.allContents.filter(EventBlock).toList
		var List<AlarmBlock> listOfAlarmBlocks = resource.allContents.filter(AlarmBlock).toList
		var List<DataPointBlock> listOfDataBlocks = resource.allContents.filter(DataPointBlock).toList
		var List<Transition> listOfTramsition = resource.allContents.filter(Transition).toList
		for (commandResponseBlock : listOfCommandResponseBlocks) {
			var compositeNode = NodeModelUtils.getNode(commandResponseBlock)
			for (node : compositeNode.asTreeIterable) {
				if (node.grammarElement instanceof CrossReference &&
					node.semanticElement instanceof CommandResponseBlock) // n.semanticElemnt is a better way (15/12/15)
				{
					highlightNode(acceptor, node, MncHighlightingConfiguration.CRB_COMMAND)
				}

			}
		}
		
		for (eventBlock : listOfEventBlocks) {
			var compositeNode = NodeModelUtils.getNode(eventBlock)
			for (node : compositeNode.asTreeIterable) {
				if (node.grammarElement instanceof CrossReference &&
					node.semanticElement instanceof EventBlock) // n.semanticElemnt is a better way (15/12/15)
				{
					highlightNode(acceptor, node, MncHighlightingConfiguration.EB_EVENT)
				}

			}
		}
		
		for (alarmBlock : listOfAlarmBlocks) {
			var compositeNode = NodeModelUtils.getNode(alarmBlock)
			for (node : compositeNode.asTreeIterable) {
				if (node.grammarElement instanceof CrossReference &&
					node.semanticElement instanceof AlarmBlock) // n.semanticElemnt is a better way (15/12/15)
				{
					highlightNode(acceptor, node, MncHighlightingConfiguration.AB_ALARM)
				}

			}
		}
		
		for (dataBlock : listOfDataBlocks) {
			var compositeNode = NodeModelUtils.getNode(dataBlock)
			for (node : compositeNode.asTreeIterable) {
				if (node.grammarElement instanceof CrossReference &&
					node.semanticElement instanceof DataPointBlock) // n.semanticElemnt is a better way (15/12/15)
				{
					highlightNode(acceptor, node, MncHighlightingConfiguration.DT_DATA)
				}

			}
		}
		
		for (transition : listOfTramsition) {
			var compositeNode = NodeModelUtils.getNode(transition)
			for (node : compositeNode.asTreeIterable) {
				if (node.grammarElement instanceof CrossReference &&
					node.semanticElement instanceof Transition) // n.semanticElemnt is a better way (15/12/15)
				{
					highlightNode(acceptor, node, MncHighlightingConfiguration.TR_STATE)
				}

			}
		}
	}

}
