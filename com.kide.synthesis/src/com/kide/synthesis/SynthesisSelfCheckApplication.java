package com.kide.synthesis;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import CapabilityDescription.Capability;
import CapabilityDescription.CapabilityDescriptionFactory;
import activityDiagramModel.Activity;
import activityDiagramModel.ActivityDiagram;
import activityDiagramModel.ActivityDiagramModelFactory;
import com.kide.knowledge.KnowledgeDataset;
import com.kide.knowledge.KnowledgeProvenance;
import com.kide.knowledge.KnowledgeTerm;
import com.kide.knowledge.KnowledgeTriple;
import com.kide.knowledge.KnowledgeVocabulary;
import mncModel.InterfaceDescription;
import mncModel.MncModelFactory;

public final class SynthesisSelfCheckApplication implements IApplication {
    @Override
    public Object start(IApplicationContext context) {
        try {
            Clock clock = Clock.systemUTC();
            ActivityDiagram diagram = ActivityDiagramModelFactory.eINSTANCE.createActivityDiagram();
            diagram.setName("SelfCheckWorkflow");

            InterfaceDescription descriptor = MncModelFactory.eINSTANCE.createInterfaceDescription();
            descriptor.setName("Camera");
            Capability capability = CapabilityDescriptionFactory.eINSTANCE.createCapability();
            capability.setName("Observe");
            capability.getComponentInterface().add(descriptor);

            Activity activity = ActivityDiagramModelFactory.eINSTANCE.createActivity();
            activity.setName("ObserveScene");
            activity.setBindCapability(capability);
            diagram.getActivities().add(activity);

            KnowledgeDataset knowledge = dataset(clock, true);
            DeterministicSynthesisService service = new DeterministicSynthesisService();
            SynthesisResult first = service.synthesize(diagram, knowledge);
            SynthesisResult second = service.synthesize(diagram, knowledge);
            if (first.status() != SynthesisStatus.SUCCESS
                    || first.controllerModel() == null
                    || !first.fingerprint().equals(second.fingerprint())
                    || !first.plan().selections().equals(second.plan().selections())) {
                throw new AssertionError("deterministic synthesis did not reproduce");
            }
            if (first.controllerModel().getSystems().size() != 2) {
                throw new AssertionError("Activity to MNC controller composition changed");
            }

            SynthesisResult noSolution = service.synthesize(diagram, dataset(clock, false));
            if (noSolution.status() != SynthesisStatus.NO_SOLUTION
                    || noSolution.controllerModel() != null) {
                throw new AssertionError("no-solution synthesis did not fail closed");
            }

            ActivityDiagram textual = ActivityDiagramModelFactory.eINSTANCE.createActivityDiagram();
            textual.setName("Textual");
            Activity textualActivity = ActivityDiagramModelFactory.eINSTANCE.createActivity();
            textualActivity.setName("ObserveScene");
            textualActivity.setRequiredCapability("Observe");
            textual.getActivities().add(textualActivity);
            SynthesisResult unbound = service.synthesize(textual, knowledge);
            if (unbound.status() != SynthesisStatus.CONTRACT_VIOLATION
                    || unbound.diagnostics().stream()
                            .noneMatch(d -> "UNBOUND_CAPABILITY_MODEL".equals(d.code()))) {
                throw new AssertionError("unbound capability semantics were invented");
            }

            System.out.println("KIDE PR36 DETERMINISTIC SYNTHESIS SELF-CHECK OK");
            return IApplication.EXIT_OK;
        } catch (Throwable failure) {
            System.err.println("KIDE PR36 synthesis self-check failed: "
                    + failure.getClass().getSimpleName());
            return Integer.valueOf(2);
        }
    }

    @Override
    public void stop() { }

    private static KnowledgeDataset dataset(Clock clock, boolean observe) {
        List<KnowledgeTriple> triples = new ArrayList<>();
        String device = "urn:kide:device:camera";
        triples.add(new KnowledgeTriple(
                device, KnowledgeVocabulary.RDF_TYPE,
                KnowledgeTerm.iri(KnowledgeVocabulary.DEVICE)));
        triples.add(new KnowledgeTriple(
                device, KnowledgeVocabulary.LABEL,
                KnowledgeTerm.literal("Camera")));
        triples.add(new KnowledgeTriple(
                device, SynthesisVocabulary.PROVIDES_CAPABILITY,
                KnowledgeTerm.iri(observe
                        ? "urn:kide:capability:Observe"
                        : "urn:kide:capability:Illuminate")));
        triples.add(new KnowledgeTriple(
                device, SynthesisVocabulary.PROVIDES_INTERFACE,
                KnowledgeTerm.iri("urn:kide:interface:Camera")));
        triples.add(new KnowledgeTriple(
                device, SynthesisVocabulary.PRIORITY, KnowledgeTerm.literal("10")));
        return new KnowledgeDataset(
                "1", "selfcheck-synthesis", "PROJECT", "selfcheck-project",
                new KnowledgeProvenance(
                        "urn:kide:selfcheck:synthesis",
                        "KIDE",
                        "selfcheck",
                        clock.millis(),
                        "INTERNAL"),
                triples);
    }
}
