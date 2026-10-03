package com.smr.activity.dsl.ide;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.ide.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalProvider;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.inject.Inject;
import com.smr.activity.dsl.validation.ActivityDiagramValidator;

import activityDiagramModel.Activity;
import activityDiagramModel.Outcome;
import mncModel.CheckParameterCondition;

/**
 * Headless equivalent of the semantic filtering performed by the Eclipse
 * Activity proposal provider.
 */
public final class KideActivityIdeContentProposalProvider
        extends IdeContentProposalProvider {

    @Inject
    private IQualifiedNameProvider names;

    @Override
    protected Predicate<IEObjectDescription> getCrossrefFilter(
            CrossReference reference,
            ContentAssistContext context) {
        Predicate<IEObjectDescription> base = super.getCrossrefFilter(reference, context);
        EObject model = model(context);
        if (model == null) {
            return base;
        }
        EReference feature = GrammarUtil.getReference(reference, model.eClass());
        if (feature == null) {
            return base;
        }

        Collection<? extends EObject> allowed = null;
        if (model instanceof Activity activity) {
            allowed = switch (feature.getName()) {
                case "useControlCapabilities" ->
                        ActivityDiagramValidator
                                .getCandidateAbstractItemsAsControlCapabilities(activity);
                case "inputParameters" ->
                        ActivityDiagramValidator
                                .getValidInputParametersForAnActivity(activity);
                default -> null;
            };
        } else if (model instanceof Outcome outcome
                && "capabilityOutcome".equals(feature.getName())) {
            allowed = ActivityDiagramValidator.getCandidateCapabilityOutcomes(outcome);
        } else if (model instanceof CheckParameterCondition condition
                && "parameter".equals(feature.getName())) {
            Outcome outcome = EcoreUtil2.getContainerOfType(condition, Outcome.class);
            if (outcome != null) {
                allowed = ActivityDiagramValidator.getCandidateParametersForOutcome(outcome);
            }
        }

        return allowed == null
                ? base
                : Predicates.and(base, allowed(allowed));
    }

    private Predicate<IEObjectDescription> allowed(Collection<? extends EObject> values) {
        Set<QualifiedName> allowed = new HashSet<>();
        for (EObject value : values) {
            QualifiedName name = names.getFullyQualifiedName(value);
            if (name != null) {
                allowed.add(name);
                if (name.getSegmentCount() > 0) {
                    allowed.add(QualifiedName.create(name.getLastSegment()));
                }
            }
        }
        return candidate -> allowed.contains(candidate.getQualifiedName());
    }

    private static EObject model(ContentAssistContext context) {
        return context.getCurrentModel() != null
                ? context.getCurrentModel()
                : context.getPreviousModel();
    }
}
